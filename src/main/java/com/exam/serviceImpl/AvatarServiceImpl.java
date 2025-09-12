package com.exam.serviceImpl;



import com.exam.dto.AvatarProfileDto;
import com.exam.repository.AvatarProfileRepository;
import com.exam.service.AvatarService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AvatarServiceImpl implements AvatarService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private AvatarProfileRepository avatarProfileRepository;

    public List<AvatarProfileDto> getTutors(String lang,Boolean kids) {
        List<AvatarProfileDto> avatarProfileDtoList = avatarProfileRepository.getAvatars(lang,kids).stream().map(avatarProfile ->{
            AvatarProfileDto avatarProfileDto = new AvatarProfileDto();
            modelMapper.map(avatarProfile, avatarProfileDto);
            return avatarProfileDto;}
        ).collect(Collectors.toList());
        return avatarProfileDtoList;
    }
    
    
}
