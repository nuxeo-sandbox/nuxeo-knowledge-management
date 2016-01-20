/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Thibaud Arguillere
 */
package org.nuxeo.km.operations;

import java.util.Calendar;

public class RandomValues {

    protected static Calendar today = Calendar.getInstance();

    protected static boolean useStaticToday = false;

    public static int randomInt(int inMin, int inMax) {
        if (inMin == inMax) {
            return inMin;
        }
        return inMin + (int) (Math.random() * ((inMax - inMin) + 1));
    }


    /**
     * Wrapper for the main method
     * <code>addDays(Calendar inDate, int inDays, boolean inMaxIsToday)</code>,
     * setting <code>inMaxIsToday</code> to <code>false</false>
     *
     * @param inDate
     * @param inDays
     * @return
     *
     * @since TODO
     */
    public static Calendar addDays(Calendar inDate, int inDays) {

        return addDays(inDate, inDays, false);

    }

    /**
     * Return a Calendar date equals to <code>inDate</code> +/-
     * <code>inDays</code>. If <code>inMaxIsToday</inMaxIsToday>
     * is true and <code>inDays</code> is positive, then "today" will be applied
     * if a random value is past "today"
     * <p>
     * Notice that despite the name "addDate", you can pass negative
     * <code>inDays</code>, so the method returns a date that is lower than
     * <code>inDate</code
     * <p>
     * <code>inDate</code> cannot be null.
     *
     * @param inDate
     * @param inDays
     * @return
     *
     * @since 7.1
     */
    public static Calendar addDays(Calendar inDate, int inDays,
            boolean inMaxIsToday) {

        if (inDays == 0) {
            return inDate;
        }

        Calendar d = (Calendar) inDate.clone();

        d.add(Calendar.DATE, inDays);
        if (inMaxIsToday) {
            if (useStaticToday) {
                if (d.after(today)) {
                    d = (Calendar) today.clone();
                }
            } else {
                Calendar newToday = Calendar.getInstance();
                if (d.after(newToday)) {
                    d = (Calendar) newToday.clone();
                }
            }
        }

        return d;
    }

    /**
     * If <code>inFrom</code> is null, the returned date is relative to "now".
     * <p>
     * To return a date which is older than <code>inFrom</code>, set
     * <code>inRewind</code> to true.
     *
     * @param inFrom, if null, the class uses "now" (depending on the
     *            <code>setUseStaticToday()</code> value)
     *
     * @since 7.1
     */
    public static Calendar buildDate(Calendar inFrom, int inDaysFrom,
            int inDaysTo, boolean inRewind) {

        Calendar result;

        if (inFrom == null) {
            if (useStaticToday) {
                result = (Calendar) today.clone();
            } else {
                result = Calendar.getInstance();
            }
        } else {
            result = (Calendar) inFrom.clone();
        }
        result.add(Calendar.DATE, RandomValues.randomInt(inDaysFrom, inDaysTo)
                * (inRewind ? -1 : 1));

        return result;
    }
}
