package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

class PostRepositoryRoomImpl(private val dao: PostDao) : PostRepository {
    private var posts = emptyList<Post>()
    private var data = MutableLiveData(posts)

    override fun get(): LiveData<List<Post>> = dao.getAll().map { list ->
        list.map { it.toDto() }
    }


    override fun save(post: Post) = dao.save(PostEntity.fromDto(post))

    override fun like(id: Long) = dao.like(id)

    override fun removeById(id: Long) = dao.removeById(id)

    override fun share(id: Long) = dao.share(id)

    override fun getById(id: Long): LiveData<Post> = dao.getById(id).map { it.toDto() }

}
