package com.thedamones.bv.queue;

import java.time.Instant;
import java.util.UUID;

public record MessageRecord(UUID id, String text, Integer dataSize, Instant timestamp) {
}
