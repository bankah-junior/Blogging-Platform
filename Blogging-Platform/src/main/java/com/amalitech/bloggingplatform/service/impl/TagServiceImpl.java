package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.model.dto.request.CreateTagRequest;
import com.amalitech.bloggingplatform.model.dto.response.TagResponse;
import com.amalitech.bloggingplatform.model.entity.Tag;
import com.amalitech.bloggingplatform.service.TagService;
import com.amalitech.bloggingplatform.utils.apis.ApiClient;
import com.amalitech.bloggingplatform.utils.apis.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class TagServiceImpl implements TagService {

    private final Gson gson = new Gson();

    @Override
    public Tag create(Tag tag) {
        if (tag == null) {
            return null;
        }
        CreateTagRequest request = new CreateTagRequest();
        request.setName(tag.getName());

        ApiResponse response = ApiClient.createTag(request);
        return parseSingleTagFromResponse(response);
    }

    @Override
    public Tag getByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        ApiResponse response = ApiClient.getTagByName(name);
        return parseSingleTagFromResponse(response);
    }

    @Override
    public List<Tag> getAll() {
        ApiResponse response = ApiClient.getAllTags();
        List<TagResponse> dtos = parseTagListFromResponse(response);
        return toTagEntities(dtos);
    }

    @Override
    public void assignTagToPost(String postId, String tagId) {
        if (postId == null || tagId == null) {
            return;
        }
        ApiClient.assignTagToPost(postId, tagId);
    }

    @Override
    public List<Tag> getTagsByPost(String postId) {
        if (postId == null) {
            return Collections.emptyList();
        }
        ApiResponse response = ApiClient.getAllTags(); // replace with a dedicated endpoint when available
        List<TagResponse> dtos = parseTagListFromResponse(response);
        return toTagEntities(dtos);
    }

    @Override
    public void unassignAllTagsFromPost(String postId) {
        if (postId == null) {
            return;
        }
        ApiClient.unassignAllTagsFromPost(postId);
    }

    // ---------- Helpers ----------

    private boolean isSuccess(ApiResponse response) {
        return response != null && "success".equalsIgnoreCase(response.getStatus());
    }

    private Tag parseSingleTagFromResponse(ApiResponse response) {
        if (!isSuccess(response) || response.getData() == null) {
            return null;
        }
        TagResponse dto = gson.fromJson(response.getData(), TagResponse.class);
        if (dto == null) {
            return null;
        }
        Tag t = new Tag();
        t.setId(dto.getId());
        t.setName(dto.getName());
        return t;
    }

    private List<TagResponse> parseTagListFromResponse(ApiResponse response) {
        if (!isSuccess(response) || response.getData() == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<TagResponse>>() {}.getType();
        return gson.fromJson(response.getData(), listType);
    }

    private List<Tag> toTagEntities(List<TagResponse> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        return dtos.stream().map(dto -> {
            Tag t = new Tag();
            t.setId(dto.getId());
            t.setName(dto.getName());
            return t;
        }).toList();
    }
}    