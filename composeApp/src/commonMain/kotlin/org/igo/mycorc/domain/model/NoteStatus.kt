package org.igo.mycorc.domain.model

enum class NoteStatus {
    DRAFT,          // Черновик (Editable, только на телефоне)
    READY_TO_SEND,  // Завершен, ждет интернета
    SENT,           // Отправлен (Read-Only на телефоне)
    APPROVED,       // Подтвержден (Деньги выплачены)
    REJECTED        // Отклонен
}