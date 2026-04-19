package gui;

import java.util.List;

public interface RouteListener {
    void onRouteChanged(List<int[]> routePoints);
    void onCurrentTargetChanged(int x, int y);
}