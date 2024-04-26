package com.ua.clear;

import com.ua.clear.dto.UserDto;
import com.ua.clear.model.User;
import com.ua.clear.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateUserSuccess() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setBirthDate(LocalDate.of(2000, 1, 1));
        userDto.setAddress("123 Test St.");
        userDto.setPhoneNumber("123-456-7890");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setAddress("123 Test St.");
        user.setPhoneNumber("123-456-7890");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void testCreateUserUnderage() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setBirthDate(LocalDate.of(2010, 1, 1)); // Underage
        userDto.setAddress("123 Test St.");
        userDto.setPhoneNumber("123-456-7890");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUpdateUserNotFound() throws Exception {
        Long nonExistingId = 999L;

        Mockito.when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDto())))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {
        Long nonExistingId = 999L;

        Mockito.when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + nonExistingId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testSearchUsersInvalidDateRange() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/search")
                        .param("from", "2025-01-01")
                        .param("to", "2024-01-01"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
