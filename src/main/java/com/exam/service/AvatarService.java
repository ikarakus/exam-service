package com.exam.service;


import com.exam.dto.AvatarProfileDto;
import java.util.List;

public interface AvatarService {

     List<AvatarProfileDto> getTutors(String lang,Boolean kids);

}

