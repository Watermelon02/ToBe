package watermelon.tobe.service.aidl;
interface TodoManager{
    void addTodo(in Todo todo);
    void deleteTodo(in Todo todo);
    void login();
    void exit();
}

