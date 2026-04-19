package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import log.Logger;

import java.io.FileOutputStream;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private static final String CONFIG_PATH =
            System.getProperty("user.home") + "/robots.properties";

    private final JDesktopPane desktopPane = new JDesktopPane();

    private LogWindow logWindow;
    private GameWindow gameWindow;
    private CoordinatesWindow coordWindow;
    private RobotModel robotModel;

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);

        robotModel = new RobotModel();

        logWindow = createLogWindow();
        addWindow(logWindow);

        this.gameWindow = new GameWindow(robotModel);
        this.coordWindow = new CoordinatesWindow(robotModel);

        gameWindow.setSize(400, 400);
        coordWindow.setLocation(420, 10);

        addWindow(gameWindow);
        addWindow(coordWindow);

        loadWindowState();

        setJMenuBar(generateMenuBar());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        //setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
// 
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
// 
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        return menuBar;
//    }
    
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JButton exitButton = new JButton("Выход");
        exitButton.setMnemonic(KeyEvent.VK_X);
        exitButton.addActionListener((event) -> {
            exitApplication();
        });

        menuBar.add(exitButton);

        JMenu routeMenu = new JMenu("Маршрут");
        routeMenu.setMnemonic(KeyEvent.VK_R);

        JMenuItem drawRouteItem = new JMenuItem("Нарисовать маршрут", KeyEvent.VK_D);
        drawRouteItem.addActionListener(e -> robotModel.startDrawingRoute());
        routeMenu.add(drawRouteItem);

        JMenuItem startRouteItem = new JMenuItem("Старт", KeyEvent.VK_S);
        startRouteItem.addActionListener(e -> {
            robotModel.finishDrawingRoute();
            robotModel.startMovingAlongRoute();
        });
        routeMenu.add(startRouteItem);

        JMenuItem clearRouteItem = new JMenuItem("Очистить", KeyEvent.VK_C);
        clearRouteItem.addActionListener(e -> robotModel.clearRoute());
        routeMenu.add(clearRouteItem);

        menuBar.add(routeMenu);

        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        {
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug("Новая строка");
            });
            testMenu.add(addLogMessageItem);
        }

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);

        return menuBar;
    }

    private void exitApplication()
    {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            saveWindowState();
            System.exit(0);
        }
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

    private void saveWindowState() {
        try {
            Properties props = new Properties();

            props.setProperty("main.x", Integer.toString(getX()));
            props.setProperty("main.y", Integer.toString(getY()));
            props.setProperty("main.w", Integer.toString(getWidth()));
            props.setProperty("main.h", Integer.toString(getHeight()));
            props.setProperty("main.state", Integer.toString(getExtendedState()));

            props.setProperty("log.x", Integer.toString(logWindow.getX()));
            props.setProperty("log.y", Integer.toString(logWindow.getY()));
            props.setProperty("log.w", Integer.toString(logWindow.getWidth()));
            props.setProperty("log.h", Integer.toString(logWindow.getHeight()));
            props.setProperty("log.icon", Boolean.toString(logWindow.isIcon()));

            props.setProperty("game.x", Integer.toString(gameWindow.getX()));
            props.setProperty("game.y", Integer.toString(gameWindow.getY()));
            props.setProperty("game.w", Integer.toString(gameWindow.getWidth()));
            props.setProperty("game.h", Integer.toString(gameWindow.getHeight()));
            props.setProperty("game.icon", Boolean.toString(gameWindow.isIcon()));

            props.setProperty("coord.x", Integer.toString(coordWindow.getX()));
            props.setProperty("coord.y", Integer.toString(coordWindow.getY()));
            props.setProperty("coord.w", Integer.toString(coordWindow.getWidth()));
            props.setProperty("coord.h", Integer.toString(coordWindow.getHeight()));
            props.setProperty("coord.icon", Boolean.toString(coordWindow.isIcon()));

            FileOutputStream out = new FileOutputStream(CONFIG_PATH);
            props.store(out, "Robots configuration");
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadWindowState() {
        try {
            FileInputStream in = new FileInputStream(CONFIG_PATH);
            Properties props = new Properties();
            props.load(in);
            in.close();

            int x = Integer.parseInt(props.getProperty("main.x", String.valueOf(getX())));
            int y = Integer.parseInt(props.getProperty("main.y", String.valueOf(getY())));
            int w = Integer.parseInt(props.getProperty("main.w", String.valueOf(getWidth())));
            int h = Integer.parseInt(props.getProperty("main.h", String.valueOf(getHeight())));
            int state = Integer.parseInt(props.getProperty("main.state", "0"));

            setBounds(x, y, w, h);
            setExtendedState(state);

            int logX = Integer.parseInt(props.getProperty("log.x", String.valueOf(logWindow.getX())));
            int logY = Integer.parseInt(props.getProperty("log.y", String.valueOf(logWindow.getY())));
            int logW = Integer.parseInt(props.getProperty("log.w", String.valueOf(logWindow.getWidth())));
            int logH = Integer.parseInt(props.getProperty("log.h", String.valueOf(logWindow.getHeight())));
            boolean logIcon = Boolean.parseBoolean(props.getProperty("log.icon", "false"));

            logWindow.setBounds(logX, logY, logW, logH);
            if (logWindow.isIcon() != logIcon){
                logWindow.setIcon(logIcon);
            }

            int gameX = Integer.parseInt(props.getProperty("game.x", String.valueOf(gameWindow.getX())));
            int gameY = Integer.parseInt(props.getProperty("game.y", String.valueOf(gameWindow.getY())));
            int gameW = Integer.parseInt(props.getProperty("game.w", String.valueOf(gameWindow.getWidth())));
            int gameH = Integer.parseInt(props.getProperty("game.h", String.valueOf(gameWindow.getHeight())));
            boolean gameIcon = Boolean.parseBoolean(props.getProperty("game.icon", "false"));

            gameWindow.setBounds(gameX, gameY, gameW, gameH);
            if (gameWindow.isIcon() != gameIcon){
                gameWindow.setIcon(gameIcon);
            }

            int coordX = Integer.parseInt(props.getProperty("coord.x", String.valueOf(coordWindow.getX())));
            int coordY = Integer.parseInt(props.getProperty("coord.y", String.valueOf(coordWindow.getY())));
            int coordW = Integer.parseInt(props.getProperty("coord.w", String.valueOf(coordWindow.getWidth())));
            int coordH = Integer.parseInt(props.getProperty("coord.h", String.valueOf(coordWindow.getHeight())));
            boolean coordIcon = Boolean.parseBoolean(props.getProperty("coord.icon", "false"));

            coordWindow.setBounds(coordX, coordY, coordW, coordH);
            if (coordWindow.isIcon() != coordIcon){
                coordWindow.setIcon(coordIcon);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Файл конфигурации не найден, используем настройки по умолчанию");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
