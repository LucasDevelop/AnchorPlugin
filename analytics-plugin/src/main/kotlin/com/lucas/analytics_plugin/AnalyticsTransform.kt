package com.lucas.analytics_plugin

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.lucas.analytics_plugin.ext.log
import com.lucas.analytics_plugin.visitor.CommonClassNode
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class AnalyticsTransform : Transform(), Plugin<Project> {

    companion object {
        const val PLUGIN_NAME = "anchor"
    }

    private  var params=GradleParamExtension()

    override fun getName(): String = "AnalyticsPlugin"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    /**
     * 指 Transform 要操作内容的范围，官方文档 Scope 有 7 种类型：
     * 1. EXTERNAL_LIBRARIES        只有外部库
     * 2. PROJECT                   只有项目内容
     * 3. PROJECT_LOCAL_DEPS        只有项目的本地依赖(本地jar)
     * 4. PROVIDED_ONLY             只提供本地或远程依赖项
     * 5. SUB_PROJECTS              只有子项目。
     * 6. SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
     * 7. TESTED_CODE               由当前变量(包括依赖项)测试的代码
     * @return
     */
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    //是否支持增量编译
    override fun isIncremental(): Boolean = true

    //注册
    override fun apply(project: Project) {
        //注册Gradle参数
        project.extensions.create(PLUGIN_NAME, GradleParamExtension::class.java)
        params = project.extensions.findByType(GradleParamExtension::class.java)?:GradleParamExtension()
        project.extensions.getByType(AppExtension::class.java).registerTransform(this)
    }

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        if (!params.enable) {
            disablePlugin(transformInvocation)
            return
        }
        "Analytics 字节码插桩开始>>>>>>>>>>>>>>>>>".log()
        val startTime = System.currentTimeMillis()
        val inputs = transformInvocation.inputs
        val outputProvider = transformInvocation.outputProvider
        val incremental = transformInvocation.isIncremental
        if (outputProvider != null && !incremental) {//删除之前输出的
            outputProvider.deleteAll()
        }
        inputs.forEach {
            it.directoryInputs.forEach {
                handlerDirInput(it, outputProvider, incremental)
            }
            it.jarInputs.forEach {
                handlerJarInput(it, outputProvider, incremental)
            }
        }
        "Analytics 总耗时：${System.currentTimeMillis() - startTime}ms".log()
    }

    private fun handlerJarInput(
        jarInput: JarInput,
        outputProvider: TransformOutputProvider,
        incremental: Boolean
    ) {
        if (incremental) {//增量
            val dest = outputProvider.getContentLocation(
                jarInput.name,
                jarInput.contentTypes,
                jarInput.scopes,
                Format.JAR
            )
            when (jarInput.status) {
                Status.NOTCHANGED -> {
                }
                Status.ADDED, Status.CHANGED -> {
                    handlerJarInputInsert(jarInput, outputProvider)
                }
                Status.REMOVED -> {
                    if (dest.exists())
                        FileUtils.forceDelete(dest)
                }
            }
        } else {
            handlerJarInputInsert(jarInput, outputProvider)
        }
    }

    //jar包插入代码
    private fun handlerJarInputInsert(jarInput: JarInput, outputProvider: TransformOutputProvider) {
        if (!jarInput.file.absolutePath.endsWith(".jar")) return
        //重名名输出文件,因为可能同名,会覆盖
        var jarName = jarInput.name
        val md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length - 4)
        }
        val jarFile = JarFile(jarInput.file)
        val enumeration = jarFile.entries()
        val tempFile = File(jarInput.file.parent + File.separator + "class_temp.jar")
        if (tempFile.exists()) {
            tempFile.delete()
        }
        val jarOutputStream = JarOutputStream(FileOutputStream(tempFile))
        while (enumeration.hasMoreElements()) {
            val jarEntry = enumeration.nextElement() as JarEntry
            val entryName = jarEntry.name
            val zipEntry = ZipEntry(entryName)
            val inputStream = jarFile.getInputStream(zipEntry)
            if (filterFile(entryName)) {
                jarOutputStream.putNextEntry(zipEntry)
                val classReader = ClassReader(IOUtils.toByteArray(inputStream))
                val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)

                val trackPageClassNode = CommonClassNode(classWriter, classReader)
                classReader.accept(trackPageClassNode, ClassReader.EXPAND_FRAMES)
                trackPageClassNode.accept(classWriter)

//                val trackMethodClassVisitor = TrackMethodClassVisitor(classReader, classWriter)
//                classReader.accept(trackMethodClassVisitor, ClassReader.EXPAND_FRAMES)

                jarOutputStream.write(classWriter.toByteArray())
            } else {
                jarOutputStream.putNextEntry(zipEntry)
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            jarOutputStream.closeEntry()
        }

        //complete
        jarOutputStream.close()
        jarFile.close()
        val dest = outputProvider.getContentLocation(
            jarName + md5Name,
            jarInput.contentTypes,
            jarInput.scopes,
            Format.JAR
        )
        FileUtils.copyFile(tempFile, dest)
        tempFile.delete()
    }

    private fun handlerDirInput(
        dirInput: DirectoryInput,
        outputProvider: TransformOutputProvider,
        incremental: Boolean
    ) {
        if (incremental) {//增量插入
            val dest = outputProvider.getContentLocation(
                dirInput.name,
                dirInput.contentTypes,
                dirInput.scopes,
                Format.DIRECTORY
            )
            FileUtils.forceMkdir(dest)
            val srcDirPath = dirInput.file.absolutePath
            val destDirPath = dest.absolutePath
            dirInput.changedFiles.forEach { changeFile ->
                val destFile = File(changeFile.key.absolutePath.replace(srcDirPath, destDirPath))
                when (changeFile.value) {
                    Status.NOTCHANGED -> {
                    }
                    Status.REMOVED -> {
                        if (destFile.exists())
                            destFile.delete()
                    }
                    Status.ADDED, Status.CHANGED -> {
                        try {
                            FileUtils.touch(destFile)
                        } catch (e: Exception) {
                        }
                        if (filterFile(changeFile.key.name)) {
//                            "dir file->${changeFile.key.absolutePath}".log()
                            val classReader = ClassReader(changeFile.key.readBytes())
                            val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)

                            val trackPageClassNode = CommonClassNode(classWriter, classReader)
                            classReader.accept(trackPageClassNode, ClassReader.EXPAND_FRAMES)
                            trackPageClassNode.accept(classWriter)

//                            val trackMethodClassVisitor = TrackMethodClassVisitor(classReader, classWriter)
//                            classReader.accept(trackMethodClassVisitor, ClassReader.EXPAND_FRAMES)

                            FileOutputStream(destFile).apply {
                                write(classWriter.toByteArray())
                                close()
                            }

                        } else {
                            if (changeFile.key.isFile) {//无需改动，复制文件
                                FileUtils.touch(destFile)
                                FileUtils.copyFile(changeFile.key, destFile)
                            }
                        }
                    }
                }
            }
        } else {//全量插入
            handlerFullDirInput(dirInput, outputProvider)
        }
    }

    //全量插入
    private fun handlerFullDirInput(
        dirInput: DirectoryInput,
        outputProvider: TransformOutputProvider
    ) {
        if (dirInput.file.isDirectory) {
            var index = 0
            com.android.utils.FileUtils.getAllFiles(dirInput.file).forEach { file ->
                if (filterFile(file.name)) {
                    val classReader = ClassReader(file.readBytes())
                    val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)

                    val trackPageClassNode = CommonClassNode(classWriter, classReader)
                    classReader.accept(trackPageClassNode, ClassReader.EXPAND_FRAMES)
                    trackPageClassNode.accept(classWriter)

//                    val trackMethodClassVisitor = TrackMethodClassVisitor(classReader, classWriter)
//                    classReader.accept(trackMethodClassVisitor, ClassReader.EXPAND_FRAMES)

                    FileOutputStream(file.parentFile.absolutePath + File.separator + file.name).apply {
                        write(classWriter.toByteArray())
                        close()
                    }
                }
            }
        }
        //处理完输入文件之后，要把输出给下一个任务
        val dest = outputProvider.getContentLocation(
            dirInput.name,
            dirInput.contentTypes,
            dirInput.scopes,
            Format.DIRECTORY
        )
        FileUtils.copyDirectory(dirInput.file, dest)
    }

    //关闭插件，文件原封不动输出
    private fun disablePlugin(transformInvocation: TransformInvocation) {
        transformInvocation.inputs.forEach {
            it.directoryInputs.forEach { dirInput ->
                transformInvocation.outputProvider.getContentLocation(
                    dirInput.name,
                    dirInput.contentTypes,
                    dirInput.scopes,
                    Format.DIRECTORY
                ).also {
                    FileUtils.copyDirectory(dirInput.file, it)
                }
            }
            it.jarInputs.forEach { jarInput ->
                transformInvocation.outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                ).also {
                    FileUtils.copyFile(jarInput.file, it)
                }
            }
        }
    }

    private fun transformFile(file: File, block: (ClassWriter) -> ClassVisitor) {
        val classReader = ClassReader(file.readBytes())
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        classReader.accept(block(classWriter), ClassReader.EXPAND_FRAMES)
        val fileOutputStream = FileOutputStream(file.absolutePath)
        fileOutputStream.write(classWriter.toByteArray())
        fileOutputStream.close()
    }

    private fun filterFile(fileName: String) =
        fileName.endsWith(".class") &&
                !fileName.startsWith("R\$") &&
                !fileName.startsWith("android/support") &&
                !fileName.startsWith("androidx/") &&
                !fileName.startsWith("com/google") &&
                fileName != "R.class" &&
                fileName != "R2.class" &&
                fileName != "BuildConfig.class"

}