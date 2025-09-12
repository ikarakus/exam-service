package com.ai.service;


import com.ai.dto.AvatarProfileDto;
import java.util.List;

public interface AvatarService {

     List<AvatarProfileDto> getTutors(String lang,Boolean kids);

}

