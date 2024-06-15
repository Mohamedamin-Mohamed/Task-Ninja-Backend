package com.example.Backend.Repositories;

import com.example.Backend.Dto.RequestDto;
import com.example.Backend.Dto.UserDto;
import com.example.Backend.Mappers.DynamoDbItemMapper;
import com.example.Backend.Utils.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserRepositoryTest {
    @Mock
    DynamoDbClient client;

    @Mock
    DynamoDbItemMapper dynamoDbItemMapper;

    @Mock
    PasswordUtils passwordUtils;

    @InjectMocks
    UserRepository userRepository;

    private UserDto userDto;
    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = new RequestDto();
        requestDto.setEmail("test@test.com");
        requestDto.setPass("test123");

        userDto = new UserDto();
        userDto.setEmail("test@test.com");
        userDto.setHashedPassword("hashedPassword123");

        MockitoAnnotations.openMocks(this);

    }

    @Test
    @DisplayName("Test to check if the user was added successfully")
    void testAddUser_Successful() {
        when(dynamoDbItemMapper.toDynamoDbItemMap(any(UserDto.class))).thenReturn(new HashMap<>());

        PutItemResponse putItemResponse = (PutItemResponse) PutItemResponse.builder().sdkHttpResponse(null).build();
        when(client.putItem(any(PutItemRequest.class))).thenReturn(putItemResponse);

        boolean result = userRepository.addUser(userDto);
        assertTrue(result);
        verify(client, times(1)).putItem(any(PutItemRequest.class));
        verify(dynamoDbItemMapper, times(1)).toDynamoDbItemMap(any(UserDto.class));
    }

    @Test
    @DisplayName("Test to check if the user wasn't added successfully")
    void testAddUser_Failed() {
        when(dynamoDbItemMapper.toDynamoDbItemMap(any(UserDto.class))).thenReturn(new HashMap<>());
        when(client.putItem(any(PutItemRequest.class))).thenThrow(DynamoDbException.class);

        boolean result = userRepository.addUser(userDto);

        assertFalse(result);
        verify(dynamoDbItemMapper, times(1)).toDynamoDbItemMap(any(UserDto.class));
        verify(client, times(1)).putItem(any(PutItemRequest.class));
    }

    @Test
    @DisplayName("Test to check if an exception was thrown")
    void testAddUser_ExceptionThrown(){
        when(dynamoDbItemMapper.toDynamoDbItemMap(any(UserDto.class))).thenReturn(new HashMap<>());
        when(client.putItem(any(PutItemRequest.class))).thenThrow(DynamoDbException.class);

        boolean result = userRepository.addUser(userDto);

        assertFalse(result);
        verify(dynamoDbItemMapper, times(1)).toDynamoDbItemMap(any(UserDto.class));
        verify(client, times(1)).putItem(any(PutItemRequest.class));
    }

    @Test
    @DisplayName("Test to check if the users password was updated successfully")
    void testUpdateUser_Successful() {
        when(passwordUtils.hashPassword(anyString())).thenReturn("hashedPassword123");
        PutItemResponse putItemResponse = (PutItemResponse) PutItemResponse.builder().sdkHttpResponse(null).build();

        when(client.putItem(any(PutItemRequest.class))).thenReturn(putItemResponse);

        boolean result = userRepository.updateUser(requestDto);

        assertTrue(result);
        verify(client, times(1)).putItem(any(PutItemRequest.class));
        verify(passwordUtils, times(1)).hashPassword(anyString());

    }

    @Test
    @DisplayName("Test to check if an exception was thrown when users password was tried to be updated")
    void testUpdateUser_ExceptionThrown() {
        when(passwordUtils.hashPassword(anyString())).thenReturn("hashedPassword123");
        when(client.putItem(any(PutItemRequest.class))).thenThrow(DynamoDbException.class);

        boolean result = userRepository.updateUser(requestDto);

        assertFalse(result);
        verify(client, times(1)).putItem(any(PutItemRequest.class));
        verify(passwordUtils, times(1)).hashPassword(anyString());
    }

    @Test
    @DisplayName("Test to check if the users password matches with the one stored in the db")
    void testPasswordMatches_True() {
        Map<String, AttributeValue> responseMap = new HashMap<>();
        responseMap.put("email", AttributeValue.builder().s("test@test.com").build());
        responseMap.put("hashedPassword", AttributeValue.builder().s("hashedPassword123").build());
        GetItemResponse getItemResponse = (GetItemResponse) GetItemResponse.builder().item(responseMap).build();

        when(client.getItem(any(GetItemRequest.class))).thenReturn(getItemResponse);
        when(passwordUtils.checkPassword(anyString(), anyString())).thenReturn(true);

        boolean result = userRepository.passwordMatches(requestDto);

        assertTrue(result);
        verify(client, times(1)).getItem(any(GetItemRequest.class));
        verify(passwordUtils, times(1)).checkPassword(anyString(), anyString());

    }

    @Test
    @DisplayName("Test to check if the users password doesn't match with the one stored in the db")
    void testPasswordMatches_False() {
        GetItemResponse getItemResponse = (GetItemResponse) GetItemResponse.builder().item(new HashMap<>()).build();
        when(client.getItem(any(GetItemRequest.class))).thenReturn(getItemResponse);

        boolean result = userRepository.passwordMatches(requestDto);

        assertFalse(result);
        verify(client, times(1)).getItem(any(GetItemRequest.class));
    }

    @Test
    @DisplayName("Test to check if an exception was thrown when the users password was tried to be updated")
    void testPasswordMatches_ExceptionThrown(){
        when(client.getItem(any(GetItemRequest.class))).thenThrow(DynamoDbException.class);

        boolean result = userRepository.passwordMatches(requestDto);

        assertFalse(result);
        verify(client, times(1)).getItem(any(GetItemRequest.class));
    }

    @Test
    @DisplayName("Test to check if the users account already exists")
    void testUserExists_True() {
        Map<String, AttributeValue> responseMap = new HashMap<>();
        responseMap.put("email", AttributeValue.builder().s("test@test.com").build());
        GetItemResponse getItemResponse = GetItemResponse.builder().item(responseMap).build();

        when(client.getItem(any(GetItemRequest.class))).thenReturn(getItemResponse);

        boolean result = userRepository.userExists(requestDto);

        assertTrue(result);
        verify(client, times(1)).getItem(any(GetItemRequest.class));
    }

    @Test
    @DisplayName("Test to check if the users account doesn't already exists")
    void testUserExists_False(){
        GetItemResponse getItemResponse = GetItemResponse.builder().item(new HashMap<>()).build();
        when(client.getItem(any(GetItemRequest.class))).thenReturn(getItemResponse);

        boolean result = userRepository.userExists(requestDto);

        assertFalse(result);
        verify(client, times(1)).getItem(any(GetItemRequest.class));
    }

    @Test
    @DisplayName("Test to check if an exception was thrown when the users account was tried being retrieved")
    void testUserExists_ExceptionThrown(){
        when(client.getItem(any(GetItemRequest.class))).thenThrow(DynamoDbException.class);

        boolean result  = userRepository.userExists(requestDto);

        assertFalse(result);
        verify(client, times(1)).getItem(any(GetItemRequest.class));
    }
}