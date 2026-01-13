package org.igo.mycorc.ui.common

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.* // Импортирует сам класс перечисления
import platform.darwin.NSObject
import platform.posix.memcpy

import platform.UIKit.UIImagePickerControllerOriginalImage

@Composable
actual fun AppImagePicker(onImagePicked: (ByteArray) -> Unit) {
    val delegate = remember {
        ImagePickerDelegate(onImagePicked)
    }

    Button(onClick = {
        launchCamera(delegate)
    }) {
        Text("Сделать фото")
    }
}

private fun launchCamera(delegate: ImagePickerDelegate) {
    val picker = UIImagePickerController()

    // Используем правильный короткий синтаксис Kotlin
    val cameraSource = UIImagePickerControllerSourceType.Camera
    val librarySource = UIImagePickerControllerSourceType.PhotoLibrary

    // ЛОГИКА: Пробуем камеру. Если нет (симулятор) — открываем галерею.
    if (UIImagePickerController.isSourceTypeAvailable(cameraSource)) {
        picker.sourceType = cameraSource
    } else {
        println("Камера недоступна (возможно, симулятор). Открываем галерею.")
        picker.sourceType = librarySource
    }

    picker.allowsEditing = false
    picker.delegate = delegate

    val keyWindow = UIApplication.sharedApplication.windows.firstOrNull { (it as UIWindow).isKeyWindow() } as? UIWindow
    val rootViewController = keyWindow?.rootViewController

    rootViewController?.presentViewController(picker, animated = true, completion = null)
}

class ImagePickerDelegate(
    private val onImagePicked: (ByteArray) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    @OptIn(ExperimentalForeignApi::class)
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage

        image?.let {
            val jpegData = UIImageJPEGRepresentation(it, 0.8)
            jpegData?.let { data ->
                onImagePicked(data.toByteArray())
            }
        }

        picker.dismissViewControllerAnimated(true, completion = null)
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
    }
}

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    return ByteArray(this.length.toInt()).apply {
        usePinned { pinned ->
            memcpy(pinned.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }
}