package org.example.expert.domain.common.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class AdminAspect {

    @Around("@annotation(org.example.expert.domain.common.annotation.Admin)")
    public Object checkAdminLog (ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes == null) {
            log.warn("[checkAdminLog] 요청 정보가 존재하지 않습니다. (RequestContextHolder == null)");
            return joinPoint.proceed();
        }

        HttpServletRequest request = requestAttributes.getRequest();

        String beforeTimeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String uri = request.getRequestURI();
        String method = request.getMethod();
        Long userId = (Long) request.getAttribute("userId");
        Object[] args = joinPoint.getArgs();
      String requestJason = Arrays.stream(args).map(arg -> {
          try {
              return new ObjectMapper().writeValueAsString(arg);
          } catch (JsonProcessingException e) {
              return "직렬화 실패";
          }
      }).collect(Collectors.joining(", "));
      log.info("[Request] {} | userId : {} {} {} {}", beforeTimeStamp, String.valueOf(userId), method, uri, requestJason);
     Object result = joinPoint.proceed();
String afterTimeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
     try{
         String responseJson = new ObjectMapper().writeValueAsString(result);
         log.info("[Response] {} | userId : {} {} {} {}", afterTimeStamp, userId, method, uri, responseJson);
     } catch (JsonProcessingException e) {
         log.warn("[Response] {} | 응답 직렬화 실패", uri);
     }
     return result;
    }
}
