package com.mrcrayfish.vehicle.util;

import net.minecraft.util.math.vector.Vector3f;

/**
 * Author: MrCrayfish
 */
public enum Axis
{
    X(Vector3f.XP, "x"),
    Y(Vector3f.YP, "y"),
    Z(Vector3f.ZP, "z");

    public static final Axis[] VALUES = values();

    private final Vector3f axis;
    private final String key;

    Axis(Vector3f axis, String key)
    {
        this.axis = axis;
        this.key = key;
    }

    public Vector3f getAxis()
    {
        return this.axis;
    }

    public String getKey()
    {
        return this.key;
    }

    public static Axis fromKey(String key)
    {
        for(Axis axis : VALUES)
        {
            if(axis.key.equals(key))
            {
                return axis;
            }
        }

        return X;
    }
}
