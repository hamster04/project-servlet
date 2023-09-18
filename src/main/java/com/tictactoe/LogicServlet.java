package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Получаем текущую сессию
        HttpSession currentSession = request.getSession();
        // Получаем объект игрового поля из сессии
        Field field = extractField(currentSession);
        // Получаем номер ячейки на которую кликнули
        int index = getNumClick(request.getParameter("click"));
        // Получаем текущий знак значения ячейки
        Sign currentSign = field.getFieldData().get(index);
        // Если она не пустая то и делать нечего, отправляем обратно
        if (currentSign != Sign.EMPTY) {
            //getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }
        // Кладем по индексу ячейки значение - крестик
        field.getField().put(index, Sign.CROSS);
        // Проверка на выигрыш
        if (checkWin(response, currentSession, field)) {
            return;
        }
        // Ищем пустую ячейку
        int emptyFieldIndex = field.getEmptyFieldIndex();
        // Если свободная ячейка для нолика
        if (emptyFieldIndex != -1) {
            // Расчет выигрышных ходов для нолика
            int indexForNought = field.calculationOfWinningMoves(index);
            field.getField().put(indexForNought, Sign.NOUGHT);
            // Проверка на выигрыш
            if (checkWin(response, currentSession, field)) {
                return;
            }
        } else {
            // Добавляем в сессию флаг, который сигнализирует что произошла ничья
            currentSession.setAttribute("draw", true);

            // Считаем список значков
            List<Sign> data = field.getFieldData();

            // Обновляем этот список в сессии
            currentSession.setAttribute("data", data);

            // Шлем редирект
            response.sendRedirect("/index.jsp");
            return;
        }
        // Обновляем объект поля и список значков в сессии
        currentSession.setAttribute("data", field.getFieldData());
        currentSession.setAttribute("field", field);

        //response.sendRedirect("/index.jsp");
        response.sendRedirect("/index.jsp");
    }
    private int getNumClick(String requestParameter) {
        try {
            return Integer.parseInt(requestParameter);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }
    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }
    /**
     * Метод проверяет, нет ли трех крестиков/ноликов в ряд.
     * Возвращает true/false
     */
    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            // Добавляем флаг, который показывает что кто-то победил
            currentSession.setAttribute("winner", winner);

            // Считаем список значков
            List<Sign> data = field.getFieldData();

            // Обновляем этот список в сессии
            currentSession.setAttribute("data", data);

            // Шлем редирект
            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}