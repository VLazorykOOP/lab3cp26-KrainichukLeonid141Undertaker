import java.util.ArrayList;
import java.util.List;

// --- VISITOR PATTERN ---

// Інтерфейс відвідувача
interface Visitor {
    void visit(HeaderElement header);
    void visit(TextElement text);
    void visit(ChartAdapter chart);
}

// Загальний інтерфейс для всіх елементів звіту
interface ReportElement {
    void render(); // Метод для відображення
    void accept(Visitor visitor); // Точка входу для відвідувача
}

// --- ABSTRACT FACTORY PATTERN ---

// Абстрактні продукти
interface HeaderElement extends ReportElement {
    String getText();
}

interface TextElement extends ReportElement {
    String getContent();
}

// Конкретні продукти: HTML Сімейство
class HTMLHeader implements HeaderElement {
    private String text;
    public HTMLHeader(String text) { this.text = text; }

    @Override
    public void render() { System.out.println("<h1>" + text + "</h1>"); }
    @Override
    public void accept(Visitor visitor) { visitor.visit(this); }
    @Override
    public String getText() { return text; }
}

class HTMLText implements TextElement {
    private String content;
    public HTMLText(String content) { this.content = content; }

    @Override
    public void render() { System.out.println("<p>" + content + "</p>"); }
    @Override
    public void accept(Visitor visitor) { visitor.visit(this); }
    @Override
    public String getContent() { return content; }
}

// Конкретні продукти: PDF Сімейство
class PDFHeader implements HeaderElement {
    private String text;
    public PDFHeader(String text) { this.text = text; }

    @Override
    public void render() { System.out.println("[PDF Header]: " + text); }
    @Override
    public void accept(Visitor visitor) { visitor.visit(this); }
    @Override
    public String getText() { return text; }
}

class PDFText implements TextElement {
    private String content;
    public PDFText(String content) { this.content = content; }

    @Override
    public void render() { System.out.println("[PDF Text Block]: " + content); }
    @Override
    public void accept(Visitor visitor) { visitor.visit(this); }
    @Override
    public String getContent() { return content; }
}

// Абстрактна фабрика
interface ReportFactory {
    HeaderElement createHeader(String text);
    TextElement createText(String content);
}

// Конкретні фабрики
class HTMLFactory implements ReportFactory {
    @Override
    public HeaderElement createHeader(String text) { return new HTMLHeader(text); }
    @Override
    public TextElement createText(String content) { return new HTMLText(content); }
}

class PDFFactory implements ReportFactory {
    @Override
    public HeaderElement createHeader(String text) { return new PDFHeader(text); }
    @Override
    public TextElement createText(String content) { return new PDFText(content); }
}

// --- ADAPTER PATTERN ---

// Сторонній клас (Legacy), який має інший інтерфейс
class LegacyAnalyticsLibrary {
    public void drawComplexGraph() {
        System.out.println(".:. Legacy Graph Data .:. ");
    }

    public String getRawGraphData() {
        return "DATA: 42, 15, 88";
    }
}

// Адаптер, який перетворює Legacy клас у зрозумілий нам ReportElement
class ChartAdapter implements ReportElement {
    private LegacyAnalyticsLibrary legacyLib;

    public ChartAdapter(LegacyAnalyticsLibrary legacyLib) {
        this.legacyLib = legacyLib;
    }

    @Override
    public void render() {
        System.out.print("Rendering Chart -> ");
        legacyLib.drawComplexGraph();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    // Метод для доступу до даних
    public String getData() {
        return legacyLib.getRawGraphData();
    }
}

// --- VISITOR IMPLEMENTATION ---

// Конкретний відвідувач: Експорт у XML формат
class XmlExportVisitor implements Visitor {
    @Override
    public void visit(HeaderElement header) {
        System.out.println("  <header>" + header.getText() + "</header>");
    }

    @Override
    public void visit(TextElement text) {
        System.out.println("  <content>" + text.getContent() + "</content>");
    }

    @Override
    public void visit(ChartAdapter chart) {
        System.out.println("  <chart source='legacy'>" + chart.getData() + "</chart>");
    }
}

public class Lab3Patterns {
    public static void main(String[] args) {
        ReportFactory factory = new HTMLFactory();

        List<ReportElement> reportPage = new ArrayList<>();

        reportPage.add(factory.createHeader("Звіт лабораторної роботи №3"));
        reportPage.add(factory.createText("Це приклад використання патернів проектування."));

        LegacyAnalyticsLibrary oldLib = new LegacyAnalyticsLibrary();
        ReportElement chart = new ChartAdapter(oldLib);
        reportPage.add(chart);

        System.out.println("--- 1. Рендеринг звіту (Abstract Factory + Adapter) ---");
        for (ReportElement element : reportPage) {
            element.render();
        }

        System.out.println("\n--- 2. Експорт в XML (Visitor) ---");
        Visitor exportVisitor = new XmlExportVisitor();

        System.out.println("<report>");
        for (ReportElement element : reportPage) {
            element.accept(exportVisitor);
        }
        System.out.println("</report>");
    }
}