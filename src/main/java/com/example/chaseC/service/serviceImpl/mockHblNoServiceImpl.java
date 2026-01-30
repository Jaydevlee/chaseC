package com.example.chaseC.service.serviceImpl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.example.chaseC.service.searchHblNoService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Profile("dev")
public class mockHblNoServiceImpl implements searchHblNoService {
   private int call = 0;
}
