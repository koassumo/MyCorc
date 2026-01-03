import SwiftUI
import ComposeApp // 👈 Импортируем наш общий модуль (имя из build.gradle)

@main
struct iOSApp: App {

    // 👇 Добавляем блок инициализации Koin
    init() {
        KoinStarterKt.startKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}