package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.recyclerview.OnInteractionListener
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryRoomImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val empty = Post(
        id = 0L,
        content = "",
        author = "",
        likedByMe = false,
        likes = 0,
        published = "",
        sharings = 0,
        video = ""
    )
    private val repository: PostRepository = PostRepositoryRoomImpl(
        AppDb.getInstance(context = application).postDao()
    )
    val data = repository.get()
    val edited = MutableLiveData(empty)


    // Функции для установки обработчика взаимодействий и переменная для хранения
    private var interactionListener: OnInteractionListener? = null
    fun setInteractionListener(listener: OnInteractionListener) {
        this.interactionListener = listener
    }

    fun getInteractionListener(): OnInteractionListener? {
        return interactionListener
    }

    fun getPostById(id: Long) = repository.getById(id)
    fun like(id: Long) = repository.like(id)
    fun share(id: Long) = repository.share(id)
    fun removeById(id: Long) = repository.removeById(id)
    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    private fun resetEditingState() {
        edited.value = empty
    }

    fun addNewPost(content: String) {
        val newPost = empty.copy(content = content.trim(), id = 0L)
        repository.save(newPost)
    }

    fun updatePost(postId: Long, content: String) {
        viewModelScope.launch {
            // Получаем текущий пост синхронно в корутине
            repository.getById(postId).value?.let { currentPost ->
                // Создаем обновленный пост
                val updatedPost = currentPost.copy(content = content.trim())

                // Сохраняем обновленный пост
                repository.save(updatedPost)

                // Сбрасываем состояние редактирования
                resetEditingState()
            }
        }
    }

}