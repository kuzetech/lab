package com.kuzetech.bigdata.design.structure.proxy;


public class App {
    public static void main(String[] args) {
        UserController controller = new UserController();

        MetricsCollectorProxy metricsCollectorProxy = new MetricsCollectorProxy();
        IUserController userController = (IUserController) metricsCollectorProxy.createProxy(controller);

        EventGeneratorProxy eventGeneratorProxy = new EventGeneratorProxy();
        IUserController realController = (IUserController) eventGeneratorProxy.createProxy(userController);

        realController.login("test", "test");
    }
}
