package com.picktartup.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.picktartup.wallet.repository.UserRepository;
import com.picktartup.wallet.service.WalletService;
import com.picktartup.wallet.utils.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class JwtWalletTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private JwtUtil jwtUtil;

  @InjectMocks
  private WalletService walletService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testGetWalletAddress_ValidTokenAndAddressFound() {
    // Given
    String token = "valid-jwt-token";
    Long userId = 123L;
    String address = "0x123456789abcdef";

    // Mock behavior for valid JWT and address found
    when(jwtUtil.validateToken(token)).thenReturn(true);
    when(jwtUtil.extractUserId(token)).thenReturn(userId.toString());
    when(userRepository.findWalletAddressByUserId(userId)).thenReturn(Optional.of(address));

    // When
    Optional<String> result = walletService.getWalletAddress(token);

    // Then
    assertTrue(result.isPresent());
    assertEquals(address, result.get());

    // Verify interactions
    verify(jwtUtil).validateToken(token);
    verify(jwtUtil).extractUserId(token);
    verify(userRepository).findWalletAddressByUserId(userId);
  }

  @Test
  public void testGetWalletAddress_InvalidToken() {
    // Given
    String token = "invalid-jwt-token";

    // Mock behavior for invalid JWT
    when(jwtUtil.validateToken(token)).thenReturn(false);

    // When
    Optional<String> result = walletService.getWalletAddress(token);

    // Then
    assertTrue(result.isEmpty());

    // Verify interactions
    verify(jwtUtil).validateToken(token);
    verify(jwtUtil, never()).extractUserId(anyString());
    verify(userRepository, never()).findWalletAddressByUserId(anyLong());
  }

  @Test
  public void testGetWalletAddress_ValidTokenButNoAddressFound() {
    // Given
    String token = "valid-jwt-token";
    Long userId = 123L;

    // Mock behavior for valid JWT but no address found
    when(jwtUtil.validateToken(token)).thenReturn(true);
    when(jwtUtil.extractUserId(token)).thenReturn(userId.toString());
    when(userRepository.findWalletAddressByUserId(userId)).thenReturn(Optional.empty());

    // When
    Optional<String> result = walletService.getWalletAddress(token);

    // Then
    assertTrue(result.isEmpty());

    // Verify interactions
    verify(jwtUtil).validateToken(token);
    verify(jwtUtil).extractUserId(token);
    verify(userRepository).findWalletAddressByUserId(userId);
  }
}
