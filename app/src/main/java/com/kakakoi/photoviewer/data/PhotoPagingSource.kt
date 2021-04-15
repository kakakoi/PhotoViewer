package com.kakakoi.photoviewer.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private const val STARTING_PAGE_INDEX = 1

class PhotoPagingSource(
    private val dao: PhotoDao
) : PagingSource<Int, Photo>() {

    companion object {
        const val TAG = "PhotoPagingSource"
    }
    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val position = params.key ?: STARTING_PAGE_INDEX

        try {
            val result = withContext(Dispatchers.IO) {
                dao.pagingPhotos(position, params.loadSize)
            }
            Log.d(TAG, "load: #2 data size ${result.size} position $position loadSize ${params.loadSize}")
            val nextKey = if (result.isEmpty()) {
                null
            } else {
                position + params.loadSize
            }

            return LoadResult.Page(
                    data = result,
                    prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = nextKey // 次のページ
                )
        } catch (e: Exception) {
            Log.e(TAG, "load: ${e.message}", )
            return LoadResult.Error(e)
        }
    }
}