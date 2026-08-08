package com.tech_eaze.hermes.service.factory;

import com.tech_eaze.hermes.service.channel.NotificationChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProviderFactoryTest {

    @Mock
    private NotificationChannel awsProvider;

    @Mock
    private NotificationChannel sendGridProvider;

    private ProviderFactory providerFactory;

    @BeforeEach
    void setUp(){
        when(awsProvider.getProviderCode()).thenReturn("AWS");
        when(sendGridProvider.getProviderCode()).thenReturn("SENDGRID");

        providerFactory = new ProviderFactory(List.of(awsProvider, sendGridProvider));
    }

    @Test
    void shouldReturnCorrectProvider_WhenGivenValidCode(){
        assertThat(providerFactory.getProvider("AWS")).isEqualTo(awsProvider);
        assertThat(providerFactory.getProvider("SENDGRID")).isEqualTo(sendGridProvider);

        assertThat(providerFactory.getProvider("aws")).isEqualTo(awsProvider);
    }

    @Test
    void shouldThrowException_WhenGivenInvalidCode(){
        assertThatThrownBy(() -> providerFactory.getProvider("UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported provider: UNKNOWN");
    }
}
