package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.exception.IntegrityViolationException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserRequestDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.utils.Validator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Validator validator;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(List<Long> ids, int from, int size) {
        log.info("The beginning of the process of finding all users");
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));
        List<User> users;

        if (CollectionUtils.isEmpty(ids)) {
            users = userRepository.findAll(pageRequest).getContent();
        } else {
            users = userRepository.findAllByIdIn(ids, pageRequest).getContent();
        }

        log.info("The user has been found");
        return userMapper.listUserToListUserDto(users);
    }

    @Override
    public UserDto createUser(UserRequestDto requestDto) {
        log.info("The beginning of the process of creating a user");
        User user = userMapper.userRequestDtoToUser(requestDto);
        userRepository.findUserByEmail(user.getEmail()).ifPresent(u -> {
            throw new IntegrityViolationException("User with email " + u.getEmail() + " already exists");
        });
        userRepository.save(user);
        log.info("The user has been created");
        return userMapper.userToUserDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        validator.checkUserId(userId);
        userRepository.deleteById(userId);
        log.info("The user has been deleted");
    }
}