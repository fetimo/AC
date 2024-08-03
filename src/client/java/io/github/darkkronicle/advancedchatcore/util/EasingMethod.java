/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.util;

/** A class to handle an easing method. Examples at <a href="https://easings.net/">Easings</a> */
public interface EasingMethod {
    /**
     * Applies the current percentage of the ease.
     *
     * @param x Double from 0-1 (will often clamp at those values)
     * @return The easing value (often clamped at 0-1)
     */
    double apply(double x);

    /** Useful easing methods */
    enum Method implements EasingMethod {
        /** Implements the linear easing function. It returns the same value. x = x */
        LINEAR(x -> x),

        /**
         * Implements the sine easing function.
         *
         * <p><a href="https://easings.net/#easeInSine">EaseInSine</a>
         */
        SINE(x -> 1 - Math.cos((x * Math.PI) / 2)),

        /**
         * Implements the quad easing function.
         *
         * <p><a href="https://easings.net/#easeInQuad">EaseInQuad</a>
         */
        QUAD(x -> x * x),

        /**
         * Implements the quart easing function.
         *
         * <p><a href="https://easings.net/#easeInQuart">EaseInQuart</a>
         */
        QUART(x -> x * x * x * x),

        /**
         * Implements the circ easing function.
         *
         * <p><a href="https://easings.net/#easeInCirc">EaseInCirc</a>
         */
        CIRC(x -> 1 - Math.sqrt(1 - Math.pow(x, 2)));

        private final EasingMethod method;

        Method(EasingMethod method) {
            this.method = method;
        }

        @Override
        public double apply(double x) {
            if (x < 0) {
                return 0;
            } else if (x > 1) {
                return 1;
            }
            return method.apply(x);
        }
    }
}
