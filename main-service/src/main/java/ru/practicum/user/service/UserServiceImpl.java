package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserRequestDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.Validator;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Validator validator;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));
        List<User> users;
        if (CollectionUtils.isEmpty(ids)) {
            users = userRepository.findAll(pageRequest).getContent();
        } else {
            users = userRepository.findAllByIdIn(ids, pageRequest).getContent();
        }
        log.info("Получение списка пользователей");
        return userMapper.toListUserDto(users);
    }

    @Override
    public UserDto createUser(UserRequestDto requestDto) {
        User user = userMapper.toUser(requestDto);
        validator.checkEmail(user);
        userRepository.save(user);
        log.info("Добавлен пользователь с id: {}", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        validator.checkUserId(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь с id: {} удалён", userId);
    }
}