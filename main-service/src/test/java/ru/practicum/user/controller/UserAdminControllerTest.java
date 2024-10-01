package ru.practicum.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserRequestDto;
import ru.practicum.user.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAdminController.class)
public class UserAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    private UserRequestDto userRequestDto;
    private UserDto userDto;

    @BeforeEach
    public void setup() {
        userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("test@test.com");
        userRequestDto.setName("User");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.com");
        userDto.setName("User");
    }

    @Test
    public void getAllUsersTest() throws Exception {
        List<UserDto> users = Collections.singletonList(userDto);
        Mockito.when(userService.find(any(List.class), anyInt(), anyInt()))
                .thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/users")
                        .param("ids", "1,2,3")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("test@test.com")))
                .andExpect(jsonPath("$[0].name", is("User")));

        Mockito.verify(userService).find(any(List.class), anyInt(), anyInt());
    }

    @Test
    public void createUserTest() throws Exception {
        Mockito.when(userService.add(any(UserRequestDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("test@test.com")))
                .andExpect(jsonPath("$.name", is("User")));

        Mockito.verify(userService).add(any(UserRequestDto.class));
    }

    @Test
    public void deleteUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/users/{userId}", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).delete(1L);
    }
}