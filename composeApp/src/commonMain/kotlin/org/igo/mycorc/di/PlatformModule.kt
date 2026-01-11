package org.igo.mycorc.di

import org.koin.core.module.Module

// Мы объявляем, что где-то (в platform-специфичном коде) будет реализован этот модуль
// для разных настроек russhwolf для каждой платформы
expect val platformModule: Module
