package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

public class WindowManagerTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new WindowManagerTest().run(args);
    }

    @Override
    protected MultiWindowTextGUI createTextGUI(Screen screen) {
        return new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen, new CustomWindowManager());
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final Window mainWindow = new BasicWindow("Window Manager Test");
        mainWindow.setComponent(new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL))
            .add(new EmptySpace(TerminalSize.ONE))
            .add(new Button("Close", s -> mainWindow.close())));
        textGUI.addWindow(mainWindow);
    }

    private static class CustomWindowManager extends DefaultWindowManager {
        @Override
        protected void prepareWindow(TerminalSize screenSize, Window window) {
            super.prepareWindow(screenSize, window);

            window.setDecoratedSize(window.getPreferredSize().withRelative(12, 10));
            window.setPosition(new TerminalPosition(
                screenSize.getColumns() - window.getDecoratedSize().getColumns() - 1,
                screenSize.getRows() - window.getDecoratedSize().getRows() - 1
            ));
        }
    }
}
