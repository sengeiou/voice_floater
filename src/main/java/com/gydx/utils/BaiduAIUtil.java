package com.gydx.utils;

import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.gydx.exception.BaiduAIException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author lenovo
 */
@Component
public class BaiduAIUtil {

    @Resource
    private AipSpeech client;

    /**
     * 语音识别录音文件转化成文字
     * @return
     */
    public String convertToCharacters(byte[] fileData) throws BaiduAIException {
        JSONObject res = client.asr(fileData, "wav", 16000, null);

        if (res.getInt("err_no") == 0) {
            return res.getJSONArray("result").getString(0);
        } else {
            System.out.println(res.getInt("err_no"));
            throw new BaiduAIException(res.getString("err_msg"));
        }
    }

    public byte[] speechSynthesis(String text, String choice) throws BaiduAIException {
        HashMap<String, Object> options = new HashMap<>(16);
        options.put("spd", "5");
        options.put("pit", "5");
        options.put("per", choice);
        TtsResponse res = client.synthesis(text, "zh", 1, options);
        JSONObject result = res.getResult();
        if (result != null) {
            throw new BaiduAIException(result.getString("err_msg"));
        } else {
            return res.getData();
        }
    }

}
