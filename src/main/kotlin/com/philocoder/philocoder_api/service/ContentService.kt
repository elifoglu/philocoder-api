package com.philocoder.philocoder_api.service

import arrow.core.Tuple2
import com.philocoder.philocoder_api.model.ContentID
import com.philocoder.philocoder_api.model.entity.Content
import com.philocoder.philocoder_api.model.entity.Tag
import com.philocoder.philocoder_api.model.request.ContentsOfTagRequest
import com.philocoder.philocoder_api.model.response.ContentResponse
import com.philocoder.philocoder_api.model.response.ContentsResponse
import com.philocoder.philocoder_api.model.response.RefDataResponse
import com.philocoder.philocoder_api.repository.ContentRepository
import com.philocoder.philocoder_api.repository.TagRepository
import org.springframework.stereotype.Service

@Service
class ContentService(
    private val repository: ContentRepository,
    private val tagRepository: TagRepository
) {

    fun getContentsResponse(req: ContentsOfTagRequest): ContentsResponse {
        val tag: Tag = tagRepository.findEntity(req.tagId)
            ?: return ContentsResponse.empty
        val contentResponses = repository.getContentsForTag(req.page, req.size, tag)
            .map { ContentResponse.createWith(it, repository) }
        val contentCount = repository.getContentCount(tag.name)
        val totalPageCount =
            if (contentCount % req.size == 0) contentCount / req.size else (contentCount / req.size) + 1
        return ContentsResponse(totalPageCount, contentResponses)
    }

    fun getRefDataResponse(): RefDataResponse {
        val allContents = repository.getEntities()
        val refTuplesIncludingContentIds: List<Tuple2<ContentID /* = kotlin.Int */, ContentID /* = kotlin.Int */>> =
            allContents
                .mapNotNull {
                    it.refs?.map { ref -> Tuple2(it.contentId, ref) }
                }
                .flatten()
        val temp = ArrayList<Content>()
        refTuplesIncludingContentIds.forEach { tuple ->
            val fromContent: Content = allContents.find { it.contentId == tuple.a }!!
            temp.add(fromContent)
            val toContent: Content = allContents.find { it.contentId == tuple.b }!!
            temp.add(toContent)
        }
        val uniqueContentsWhichArePartOfAtLeastOneReference = temp.distinctBy { it.contentId }
        val letterCountToShow = 100
        val uniqueTitlesOfContentsWhichArePartsOfAtLeastOneReference: List<String> =
            uniqueContentsWhichArePartOfAtLeastOneReference.map {
                it.title ?: it.content!!.take(letterCountToShow)
                    .trim() + if (it.content!!.length > letterCountToShow) "..." else ""

            }
        val uniqueIdsOfContentsWhichArePartOfAtLeastOneReference =
            uniqueContentsWhichArePartOfAtLeastOneReference.map { it.contentId }
        val refTuplesIncludingIndexes =
            refTuplesIncludingContentIds.map { (fromContentId, toContentId) ->
                val fromIndex = uniqueIdsOfContentsWhichArePartOfAtLeastOneReference.indexOf(fromContentId)
                val toIndex = uniqueIdsOfContentsWhichArePartOfAtLeastOneReference.indexOf(toContentId)
                Tuple2(fromIndex, toIndex)
            }
        return RefDataResponse(
            uniqueTitlesOfContentsWhichArePartsOfAtLeastOneReference,
            uniqueIdsOfContentsWhichArePartOfAtLeastOneReference,
            refTuplesIncludingIndexes
        )
    }
}