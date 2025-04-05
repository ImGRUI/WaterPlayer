package ru.kelcuprum.waterplayer.backend;

import net.minecraft.client.KeyMapping;

public record KeyBind(KeyMapping key, Execute onExecute) {
    public interface Execute {
        boolean run();
    }
}
