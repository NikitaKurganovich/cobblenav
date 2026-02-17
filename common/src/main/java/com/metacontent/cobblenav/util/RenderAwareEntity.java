package com.metacontent.cobblenav.util;

public interface RenderAwareEntity {
    boolean cobblenav$isRendered();

    void cobblenav$setRendered(boolean isRendered);
}
