package com.moonstub.training.app.snakealpha.Screens;

import android.graphics.Color;

import com.moonstub.training.app.snakealpha.framework.GameGraphics;
import com.moonstub.training.app.snakealpha.framework.GameSettings;

import java.util.ArrayList;

/**
 * Created by Micah on 11/9/2015.
 */
public class Section {

    int x, y;
    int type = 0;

    public Section(int x, int y, int type) {
        this.x = x * GameSettings.GRID_SIZE;
        this.y = y * GameSettings.GRID_SIZE;
        this.type = type;
    }

    public Section() {

    }

    public void draw(GameGraphics g) {
        if (type == 0)
            g.drawRect(x, y, GameSettings.GRID_SIZE, GameSettings.GRID_SIZE, Color.BLUE);
        if (type == 1)
            g.drawRect(x, y, GameSettings.GRID_SIZE, GameSettings.GRID_SIZE, Color.GREEN);
    }

    public boolean checkCollisionList(ArrayList<Section> sectionList) {
        int len = sectionList.size();
        for (int i = 0; i < len; i++) {
            if (x == sectionList.get(i).x && y == sectionList.get(i).y) {
                return true;
            }
        }
        return false;
    }

    public boolean checkCollisionSelf(Section section) {
        int x1 = section.x;
        int y1 = section.y;
        if (x == x1 && y == y1) {
            return true;
        } else {
            return false;
        }
    }


    public void setPoint(int x, int y) {
        this.x=x;
        this.y=y;
    }
}
