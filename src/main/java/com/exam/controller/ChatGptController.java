package com.exam.controller;

import com.exam.dto.*;
import com.exam.service.*;
import com.exam.util.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/gpt")
public class ChatGptController {

    @Autowired
    OpenAiService openAiService;

    @Autowired
    AvatarService avatarService;


    @PostMapping("/chat")
    public ResponseEntity<ResponseDto> chat(@RequestBody Chat chat) {
        ResponseDto responseDto = new ResponseDto<>();
        ChatResponse chatResponse = openAiService.callOpenAiApi(chat.getModel(), chat.getPrompt(), chat.getLanguage(), chat.getLanguageLevel(), chat.getTopic(), chat.getTutor(), chat.getPastDialogue());
        responseDto.setResponseBody(Collections.singletonList(chatResponse));
        Helper.fillResponse(responseDto, ResultCodes.OK, null);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/tutors/{lang}/{kids}")
    public ResponseEntity<ResponseDto> getTutors(@PathVariable String lang, @PathVariable String kids) {
        ResponseDto responseDto = new ResponseDto<>();
        List<AvatarProfileDto> avatarProfileDtoList = avatarService.getTutors(lang,Boolean.parseBoolean(kids));
        responseDto.setResponseBody(avatarProfileDtoList);
        Helper.fillResponse(responseDto, ResultCodes.OK, null);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


}


