package me.clockclap.tct.api.event;

import me.clockclap.tct.game.TCTGame;
import org.bukkit.event.HandlerList;

public class GameItemDistributeEvent extends GameEvent {

    private static final HandlerList handlers = new HandlerList();

    public GameItemDistributeEvent(TCTGame game) {
        super(game);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
