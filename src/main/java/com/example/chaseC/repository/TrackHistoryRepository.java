package com.example.chaseC.repository;

import com.example.chaseC.entity.TrackHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.sound.midi.Track;
import java.util.List;

public interface TrackHistoryRepository extends JpaRepository<TrackHistory, Long> {
}
