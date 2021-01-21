package com.gydx.mapper;

import com.gydx.dto.FloaterSubmitDTO;
import com.gydx.entity.Recording;

import java.util.List;

/**
 * @author 拽小白
 */
public interface RecordingMapper {
    void save(Recording recording);

    void submit(FloaterSubmitDTO floaterSubmitDTO);

    Recording findById(String recordingId);

    List<String> findIdListByUserId(String userId);

    List<Recording> findAll();

    List<Recording> findByUserId(String userId);
}
