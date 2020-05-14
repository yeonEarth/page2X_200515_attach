package com.example.page2x_200514_mj;

import java.util.ArrayList;

public interface Page2_X_Interface {
    void onClick(double x, double y, String name);
    void make_db(String countId, String name);
    void make_dialog();
    void onData(ArrayList<Page2_X_CategoryBottom.Category_item> text);
}
