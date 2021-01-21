package com.gydx.service;

import com.gydx.dto.FloaterSubmitDTO;
import com.gydx.entity.Recording;
import io.swagger.models.auth.In;

import java.io.InputStream;
import java.util.List;

public interface RecordingService {
    Integer saveTypeOne(InputStream inputStream, String userId);

    Integer saveTypeTwo(String url, String userId);

    void submit(FloaterSubmitDTO floaterSubmitDTO);

    Recording findById(String recordingId);

    List<String> findIdListByUserId(String userId);

    List<Recording> findAll();

    List<Recording> findByUserId(String userId);
}
