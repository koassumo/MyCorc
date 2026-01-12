package org.igo.mycorc.ui.common

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.*
import platform.darwin.NSObject
import platform.posix.memcpy

// 👇 ЯВНЫЕ ИМПОРТЫ РЕШАЮТ ПРОБЛЕМУ "Unresolved reference"
import platform.UIKit.UIImagePickerControllerSourceTypeCamera
import platform.UIKit.UIImagePickerControllerOriginalImage

@Composable
actual fun AppImagePicker(onImagePicked: (ByteArray) -> Unit) {
    // Создаем делегат, который будет обрабатывать результат камеры
    val delegate = remember {
        ImagePickerDelegate(onImagePicked)
    }

    Button(onClick = {
        launchCamera(delegate)
    }) {
        Text("Сделать фото")
    }
}

// Функция запуска камеры
private fun launchCamera(delegate: ImagePickerDelegate) {
    val picker = UIImagePickerController()

    // ПРОВЕРЯЕМ: Если камера доступна, включаем её.
    if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceTypeCamera)) {
        picker.sourceType = UIImagePickerControllerSourceTypeCamera
    } else {
        // На симуляторе камеры нет
        println("Камера недоступна на этом устройстве")
        // Можно раскомментировать строку ниже, чтобы на симуляторе открывалась галерея для тестов:
        // picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary
        return
    }

    // Отключаем редактирование (кроп и т.д.)
    picker.allowsEditing = false
    picker.delegate = delegate

    // Находим текущий экран (ViewController), чтобы показать поверх него камеру
    val keyWindow = UIApplication.sharedApplication.windows.firstOrNull { (it as UIWindow).isKeyWindow() } as? UIWindow
    val rootViewController = keyWindow?.rootViewController

    rootViewController?.presentViewController(picker, animated = true, completion = null)
}

// Делегат (обработчик событий камеры)
class ImagePickerDelegate(
    private val onImagePicked: (ByteArray) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    @OptIn(ExperimentalForeignApi::class)
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        // 1. Достаем фото из результата (теперь константа точно найдется)
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage

        image?.let {
            // 2. Конвертируем UIImage в JPEG (качество 0.8)
            val jpegData = UIImageJPEGRepresentation(it, 0.8)
            jpegData?.let { data ->
                // 3. Превращаем в ByteArray и отдаем в Compose
                onImagePicked(data.toByteArray())
            }
        }

        // 4. Закрываем шторку камеры
        picker.dismissViewControllerAnimated(true, completion = null)
    }

    // Если пользователь нажал "Отмена"
    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
    }
}

// Вспомогательная функция: превращает iOS NSData в Kotlin ByteArray
@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    return ByteArray(this.length.toInt()).apply {
        usePinned { pinned ->
            memcpy(pinned.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }
}