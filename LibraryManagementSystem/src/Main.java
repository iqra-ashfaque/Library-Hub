import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Main extends JFrame {

    // ================= DATABASE =================
    private static final String URL =
            "jdbc:mysql://localhost:3306/librarydb";
    private static final String USER = "root";
    private static final String PASSWORD = "Iqra";

    // ===== NEW: folder jahan book covers save honge =====
    private static final String COVERS_DIR = "covers";

    // ================= COLORS =================
    private final Color BG = new Color(238, 234, 255);
    private final Color SIDEBAR = new Color(43, 35, 85);
    private final Color PRIMARY = new Color(124, 92, 231);
    private final Color PINK = new Color(236, 91, 155);
    private final Color BLUE = new Color(76, 132, 255);
    private final Color GREEN = new Color(58, 190, 130);
    private final Color ORANGE = new Color(255, 170, 70);
    private final Color RED = new Color(239, 90, 90);
    private final Color TEXT = new Color(45, 38, 75);
    private final Color WHITE = Color.WHITE;

    private JPanel contentPanel;

    public Main() {
        setTitle("Library Hub - Library Management System");
        setSize(1350, 800);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createMainWindow();
        showDashboard();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ================= MAIN WINDOW =================
    private void createMainWindow() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);

        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(245, 800));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("LIBRARY HUB");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 22));
        logo.setBorder(new EmptyBorder(30, 24, 30, 10));
        sidebar.add(logo);

        JButton dashboard = sideButton("Dashboard");
        JButton books = sideButton("Books");
        JButton students = sideButton("Students");
        JButton issue = sideButton("Issue Book");
        JButton issued = sideButton("Issued Books");
        JButton returned = sideButton("Returned Books");
        JButton addStudent = sideButton("Add Student");
        JButton returnBook = sideButton("Return Book");

        sidebar.add(dashboard);
        sidebar.add(books);
        sidebar.add(students);
        sidebar.add(issue);
        sidebar.add(issued);
        sidebar.add(returned);
        sidebar.add(addStudent);
        sidebar.add(returnBook);
        sidebar.add(Box.createVerticalGlue());

        JButton exit = sideButton("Exit");
        sidebar.add(exit);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG);

        main.add(sidebar, BorderLayout.WEST);
        main.add(contentPanel, BorderLayout.CENTER);
        setContentPane(main);

        dashboard.addActionListener(e -> showDashboard());
        books.addActionListener(e -> showBooks());
        students.addActionListener(e -> showStudents());
        issue.addActionListener(e -> issueBookDialog());
        issued.addActionListener(e -> showIssuedBooks());
        returned.addActionListener(e -> showReturnedBooks());
        addStudent.addActionListener(e -> addStudentDialog());
        returnBook.addActionListener(e -> returnBookDialog());
        exit.addActionListener(e -> System.exit(0));
    }

    private JButton sideButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(245, 52));
        button.setPreferredSize(new Dimension(245, 52));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setForeground(Color.WHITE);
        button.setBackground(SIDEBAR);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setBorder(new EmptyBorder(10, 28, 10, 10));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(65, 54, 115));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(SIDEBAR);
            }
        });
        return button;
    }

    // ================= DASHBOARD =================
    private void showDashboard() {
        contentPanel.removeAll();

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(30, 35, 20, 35));

        JPanel heading = new JPanel();
        heading.setBackground(BG);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome to Library Hub");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Manage books, students, issues and returns in one place.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitle.setForeground(new Color(105, 98, 125));

        heading.add(title);
        heading.add(Box.createVerticalStrut(8));
        heading.add(subtitle);
        header.add(heading, BorderLayout.WEST);
        page.add(header, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(1, 4, 18, 18));
        stats.setBackground(BG);
        stats.setBorder(new EmptyBorder(10, 35, 25, 35));

        stats.add(statCard("BOOKS", "Total Books", getCount("SELECT COUNT(*) FROM Books"), PRIMARY));
        stats.add(statCard("STUDENTS", "Students", getCount("SELECT COUNT(*) FROM Students"), BLUE));
        stats.add(statCard("ISSUED", "Issued", getCount("SELECT COUNT(*) FROM Issue_Return WHERE status='Issued'"), ORANGE));
        stats.add(statCard("RETURNED", "Returned", getCount("SELECT COUNT(*) FROM Issue_Return WHERE status='Returned'"), GREEN));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);
        center.add(stats, BorderLayout.NORTH);

        JPanel welcome = new JPanel();
        welcome.setBackground(WHITE);
        welcome.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 214, 235)),
                new EmptyBorder(35, 35, 35, 35)
        ));
        welcome.setLayout(new BoxLayout(welcome, BoxLayout.Y_AXIS));

        JLabel big = new JLabel("Your library, beautifully organized.");
        big.setFont(new Font("Arial", Font.BOLD, 24));
        big.setForeground(TEXT);
        big.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel info = new JLabel("Use the menu on the left to manage everything.");
        info.setFont(new Font("Arial", Font.PLAIN, 16));
        info.setForeground(Color.GRAY);
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        welcome.add(Box.createVerticalGlue());
        welcome.add(big);
        welcome.add(Box.createVerticalStrut(10));
        welcome.add(info);
        welcome.add(Box.createVerticalGlue());

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG);
        wrap.setBorder(new EmptyBorder(0, 35, 35, 35));
        wrap.add(welcome, BorderLayout.CENTER);
        center.add(wrap, BorderLayout.CENTER);

        page.add(center, BorderLayout.CENTER);
        contentPanel.add(page);
        refresh();
    }

    private JPanel statCard(String small, String name, int number, Color color) {
        JPanel card = new JPanel();
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 220, 235)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel s = new JLabel(small);
        s.setForeground(color);
        s.setFont(new Font("Arial", Font.BOLD, 12));
        s.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel n = new JLabel(String.valueOf(number));
        n.setForeground(TEXT);
        n.setFont(new Font("Arial", Font.BOLD, 30));
        n.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel t = new JLabel(name);
        t.setForeground(Color.GRAY);
        t.setFont(new Font("Arial", Font.PLAIN, 14));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(s);
        card.add(Box.createVerticalStrut(8));
        card.add(n);
        card.add(Box.createVerticalStrut(5));
        card.add(t);
        return card;
    }

    // ================= BOOKS =================
    private void showBooks() {
        contentPanel.removeAll();

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(25, 30, 15, 30));

        JLabel title = new JLabel("All Books");
        title.setFont(new Font("Arial", Font.BOLD, 29));
        title.setForeground(TEXT);

        JButton add = new JButton("+ Add Book");
        styleButton(add, PRIMARY);
        add.addActionListener(e -> addBookDialog());

        header.add(title, BorderLayout.WEST);
        header.add(add, BorderLayout.EAST);
        page.add(header, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(BG);
        searchPanel.setBorder(new EmptyBorder(0, 30, 15, 30));

        JTextField search = new JTextField();
        search.setFont(new Font("Arial", Font.PLAIN, 15));
        search.setBackground(Color.WHITE);
        search.setForeground(TEXT);
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 203, 230)),
                new EmptyBorder(10, 12, 10, 12)
        ));
        search.setToolTipText("Search by book title");
        searchPanel.add(search);

        JPanel grid = new JPanel(new GridLayout(0, 4, 18, 18));
        grid.setBackground(BG);
        grid.setBorder(new EmptyBorder(5, 30, 30, 30));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);
        center.add(searchPanel, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        page.add(center, BorderLayout.CENTER);

        loadBooks(grid, "");

        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() { loadBooks(grid, search.getText().trim()); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        contentPanel.add(page);
        refresh();
    }

    private void loadBooks(JPanel grid, String search) {
        grid.removeAll();
        String sql = "SELECT b.book_id, b.title, a.author_name, b.quantity, b.cover_path " +
                "FROM Books b LEFT JOIN Authors a ON b.author_id=a.author_id " +
                "WHERE b.title LIKE ? ORDER BY b.book_id DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + search + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grid.add(createBookCard(
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getString("author_name"),
                            rs.getInt("quantity"),
                            rs.getString("cover_path")
                    ));
                }
            }
        } catch (Exception e) {
            showError("Unable to load books:\n" + e.getMessage());
        }

        grid.revalidate();
        grid.repaint();
    }

    private JPanel createBookCard(int id, String title, String author, int quantity, String coverPath) {
        Color[] cardColors = {
                new Color(255, 239, 246),
                new Color(238, 242, 255),
                new Color(239, 255, 247),
                new Color(255, 247, 230),
                new Color(246, 239, 255)
        };
        Color cardColor = cardColors[Math.abs(title.hashCode()) % cardColors.length];

        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 214, 230)),
                new EmptyBorder(12, 12, 14, 12)
        ));

        JLabel cover = new JLabel();
        cover.setHorizontalAlignment(SwingConstants.CENTER);
        // NEW: pehle real cover try karo, na mile to purana generated cover
        BufferedImage realCover = loadCover(coverPath, 175, 225);
        cover.setIcon(new ImageIcon(realCover != null
                ? realCover
                : createOfflineCover(title, author, 175, 225)));
        cover.setPreferredSize(new Dimension(175, 225));
        card.add(cover, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setBackground(cardColor);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("<html><div style='text-align:center;width:175px;'>" +
                escapeHtml(title) + "</div></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titleLabel.setForeground(TEXT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel authorLabel = new JLabel(author == null ? "Unknown Author" : author);
        authorLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        authorLabel.setForeground(new Color(100, 90, 120));
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel copies = new JLabel("Available: " + quantity);
        copies.setFont(new Font("Arial", Font.BOLD, 13));
        copies.setForeground(quantity > 0 ? GREEN : RED);
        copies.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton delete = new JButton("Delete");
        styleButton(delete, RED);
        delete.setAlignmentX(Component.CENTER_ALIGNMENT);
        delete.addActionListener(e -> deleteBook(id));

        info.add(titleLabel);
        info.add(Box.createVerticalStrut(5));
        info.add(authorLabel);
        info.add(Box.createVerticalStrut(5));
        info.add(copies);
        info.add(Box.createVerticalStrut(9));

        JButton changeCover = new JButton("Change Cover");
        styleButton(changeCover, BLUE);
        changeCover.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeCover.addActionListener(e -> changeCoverDialog(id));

        info.add(changeCover);
        info.add(Box.createVerticalStrut(6));
        info.add(delete);
        card.add(info, BorderLayout.CENTER);

        return card;
    }

    // ================= REAL BOOK COVER (NEW) =================
    // DB me sirf path save hota hai (jaise "covers/123_harry.jpg").
    // Image load nahi hui ya path khali hai to null return hota hai.
    private BufferedImage loadCover(String path, int width, int height) {
        if (path == null || path.trim().isEmpty()) return null;
        try {
            File f = new File(path);
            if (!f.exists()) return null;
            BufferedImage src = ImageIO.read(f);
            if (src == null) return null;

            // Center-crop karke card ke size me fit (stretch nahi hoga)
            double scale = Math.max((double) width / src.getWidth(),
                    (double) height / src.getHeight());
            int w = (int) Math.round(src.getWidth() * scale);
            int h = (int) Math.round(src.getHeight() * scale);

            BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = out.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(src, (width - w) / 2, (height - h) / 2, w, h, null);
            g.dispose();
            return out;
        } catch (Exception e) {
            System.out.println("Cover load error: " + e.getMessage());
            return null;
        }
    }

    // File chooser kholta hai (sirf images). Cancel par null.
    private File chooseCoverFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Book Cover");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Images (jpg, jpeg, png, gif)", "jpg", "jpeg", "png", "gif"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            // Check karo ke Java is image ko padh sakta hai ya nahi (webp/avif nahi padh sakta)
            try {
                if (ImageIO.read(f) == null) {
                    showError("This image format is not supported:\n" + f.getName() +
                            "\n\nPlease convert it to JPG or PNG and try again.");
                    return null;
                }
            } catch (Exception e) {
                showError("Could not read this image:\n" + f.getName() +
                        "\n\nPlease convert it to JPG or PNG and try again.");
                return null;
            }
            return f;
        }
        return null;
    }

    // Chosen image ko covers folder me copy karta hai aur relative path return karta hai
    private String saveCoverToFolder(File source) throws Exception {
        File dir = new File(COVERS_DIR);
        if (!dir.exists()) dir.mkdirs();

        String safeName = System.currentTimeMillis() + "_" +
                source.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
        File target = new File(dir, safeName);
        Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return COVERS_DIR + "/" + safeName;
    }

    // Existing book ka cover badalne ke liye
    private void changeCoverDialog(int bookId) {
        File file = chooseCoverFile();
        if (file == null) return;

        try {
            String path = saveCoverToFolder(file);
            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "UPDATE Books SET cover_path=? WHERE book_id=?")) {
                ps.setString(1, path);
                ps.setInt(2, bookId);
                ps.executeUpdate();
            }
            showBooks();
        } catch (Exception e) {
            showError("Cover could not be updated:\n" + e.getMessage());
        }
    }

    // ================= OFFLINE BOOK COVER =================
    private BufferedImage createOfflineCover(String title, String author, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Color[] palettes = {
                new Color(93, 72, 164), new Color(218, 79, 132),
                new Color(49, 126, 189), new Color(41, 150, 115),
                new Color(231, 145, 58), new Color(116, 83, 154)
        };
        Color base = palettes[Math.abs(title.toLowerCase().hashCode()) % palettes.length];
        Color lighter = base.brighter();

        g.setColor(base);
        g.fillRect(0, 0, width, height);

        g.setColor(new Color(255, 255, 255, 35));
        for (int i = -height; i < width + height; i += 35) {
            g.rotate(Math.toRadians(20), width / 2.0, height / 2.0);
            g.fillRect(i, 0, 18, height);
            g.rotate(Math.toRadians(-20), width / 2.0, height / 2.0);
        }

        g.setColor(new Color(255, 255, 255, 220));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(12, 12, width - 24, height - 24, 12, 12);

        g.setFont(new Font("Arial", Font.BOLD, 12));
        drawCentered(g, "LIBRARY HUB", width / 2, 34, Color.WHITE);

        String[] lines = splitTitle(title, 16, 3);
        int y = height / 2 - ((lines.length - 1) * 16);
        for (String line : lines) {
            g.setFont(new Font("Arial", Font.BOLD, line.length() > 18 ? 15 : 19));
            drawCentered(g, line, width / 2, y, Color.WHITE);
            y += 24;
        }

        g.setColor(new Color(255, 255, 255, 210));
        g.fillRoundRect(30, height - 75, width - 60, 2, 2, 2);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String a = author == null ? "Unknown Author" : author;
        if (a.length() > 25) a = a.substring(0, 25) + "...";
        drawCentered(g, a, width / 2, height - 49, Color.WHITE);

        g.setFont(new Font("Arial", Font.BOLD, 25));
        drawCentered(g, "BOOK", width / 2, height - 18, lighter);
        g.dispose();
        return image;
    }

    private String[] splitTitle(String title, int maxChars, int maxLines) {
        String[] words = title.trim().split("\\s+");
        ArrayList<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            if (current.length() + word.length() + 1 <= maxChars) {
                if (current.length() > 0) current.append(' ');
                current.append(word);
            } else {
                if (current.length() > 0) lines.add(current.toString());
                current = new StringBuilder(word);
            }
        }
        if (current.length() > 0) lines.add(current.toString());

        if (lines.size() > maxLines) {
            ArrayList<String> shortened = new ArrayList<>();
            for (int i = 0; i < maxLines - 1; i++) shortened.add(lines.get(i));
            String last = lines.get(maxLines - 1);
            shortened.add(last.length() > maxChars - 3 ? last.substring(0, maxChars - 3) + "..." : last);
            return shortened.toArray(new String[0]);
        }
        return lines.toArray(new String[0]);
    }

    private void drawCentered(Graphics2D g, String text, int x, int y, Color color) {
        FontMetrics fm = g.getFontMetrics();
        g.setColor(color);
        g.drawString(text, x - fm.stringWidth(text) / 2, y);
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // ================= ADD BOOK =================
    private void addBookDialog() {
        JTextField title = new JTextField();
        JTextField author = new JTextField();
        JTextField quantity = new JTextField("1");

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.add(new JLabel("Book Title:"));
        panel.add(title);
        panel.add(new JLabel("Author:"));
        panel.add(author);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantity);

        // NEW: Cover select karne ka option (optional)
        final File[] selectedCover = new File[1];
        JLabel coverName = new JLabel("No cover selected");
        coverName.setForeground(Color.GRAY);
        JButton browse = new JButton("Choose Cover...");
        styleButton(browse, BLUE);
        browse.addActionListener(ev -> {
            File f = chooseCoverFile();
            if (f != null) {
                selectedCover[0] = f;
                coverName.setText(f.getName());
            }
        });
        JPanel coverRow = new JPanel(new BorderLayout(8, 0));
        coverRow.add(browse, BorderLayout.WEST);
        coverRow.add(coverName, BorderLayout.CENTER);
        panel.add(new JLabel("Cover (optional):"));
        panel.add(coverRow);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Add New Book", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) return;

        String bookTitle = title.getText().trim();
        String bookAuthor = author.getText().trim();
        int qty;
        try {
            qty = Integer.parseInt(quantity.getText().trim());
        } catch (Exception e) {
            showError("Please enter a valid quantity.");
            return;
        }

        if (bookTitle.isEmpty() || bookAuthor.isEmpty() || qty < 1) {
            showError("Book title, author and a quantity of at least 1 are required.");
            return;
        }

        String findAuthor = "SELECT author_id FROM Authors WHERE author_name=?";
        String insertAuthor = "INSERT INTO Authors (author_name) VALUES (?)";
        String insertBook = "INSERT INTO Books (title, author_id, quantity, cover_path) VALUES (?, ?, ?, ?)";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            int authorId;

            try (PreparedStatement ps = con.prepareStatement(findAuthor)) {
                ps.setString(1, bookAuthor);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        authorId = rs.getInt(1);
                    } else {
                        try (PreparedStatement addAuthor = con.prepareStatement(
                                insertAuthor, Statement.RETURN_GENERATED_KEYS)) {
                            addAuthor.setString(1, bookAuthor);
                            addAuthor.executeUpdate();
                            try (ResultSet keys = addAuthor.getGeneratedKeys()) {
                                if (!keys.next()) throw new SQLException("Could not create author.");
                                authorId = keys.getInt(1);
                            }
                        }
                    }
                }
            }

            try (PreparedStatement book = con.prepareStatement(insertBook)) {
                book.setString(1, bookTitle);
                book.setInt(2, authorId);
                book.setInt(3, qty);
                // NEW: cover copy karke path save
                if (selectedCover[0] != null) {
                    book.setString(4, saveCoverToFolder(selectedCover[0]));
                } else {
                    book.setNull(4, Types.VARCHAR);
                }
                book.executeUpdate();
            }

            con.commit();
            JOptionPane.showMessageDialog(this, "Book added successfully!");
            showBooks();
        } catch (Exception e) {
            showError("Book could not be added:\n" + e.getMessage());
        }
    }

    // ================= DELETE BOOK =================
    private void deleteBook(int bookId) {
        int choice = JOptionPane.showConfirmDialog(
                this, "Delete this book?", "Confirm Delete", JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) return;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Books WHERE book_id=?")) {
            ps.setInt(1, bookId);
            ps.executeUpdate();
            showBooks();
        } catch (Exception e) {
            showError("This book cannot be deleted if it has issue/return records.\n\n" + e.getMessage());
        }
    }

    // ================= STUDENTS =================
    private void showStudents() {
        contentPanel.removeAll();
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(25, 30, 15, 30));

        JLabel title = new JLabel("Students");
        title.setFont(new Font("Arial", Font.BOLD, 29));
        title.setForeground(TEXT);

        JButton add = new JButton("+ Add Student");
        styleButton(add, BLUE);
        add.addActionListener(e -> addStudentDialog());

        header.add(title, BorderLayout.WEST);
        header.add(add, BorderLayout.EAST);
        page.add(header, BorderLayout.NORTH);

        JTable table = new JTable(getStudentRows(), new String[]{"ID", "Name", "Email", "Phone"});
        styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 30, 30, 30));
        page.add(scroll, BorderLayout.CENTER);

        contentPanel.add(page);
        refresh();
    }

    private Object[][] getStudentRows() {
        ArrayList<Object[]> rows = new ArrayList<>();
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT student_id,name,email,phone FROM Students ORDER BY student_id DESC")) {
            while (rs.next()) {
                rows.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
        return rows.toArray(new Object[0][]);
    }

    private void addStudentDialog() {
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField phone = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.add(new JLabel("Name:"));
        panel.add(name);
        panel.add(new JLabel("Email:"));
        panel.add(email);
        panel.add(new JLabel("Phone:"));
        panel.add(phone);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Add Student", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) return;
        if (name.getText().trim().isEmpty()) {
            showError("Student name is required.");
            return;
        }

        String sql = "INSERT INTO Students (name,email,phone) VALUES (?,?,?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.getText().trim());
            ps.setString(2, email.getText().trim());
            ps.setString(3, phone.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student added successfully!");
            showStudents();
        } catch (Exception e) {
            showError("Student could not be added:\n" + e.getMessage());
        }
    }

    // ================= ISSUE BOOK =================
    private void issueBookDialog() {
        JComboBox<StudentItem> students = new JComboBox<>();
        JComboBox<BookItem> books = new JComboBox<>();
        loadStudents(students);
        loadAvailableBooks(books);

        if (students.getItemCount() == 0) {
            showError("No students found. Please add a student first.");
            return;
        }
        if (books.getItemCount() == 0) {
            showError("No available books found.");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.add(new JLabel("Student:"));
        panel.add(students);
        panel.add(new JLabel("Book:"));
        panel.add(books);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Issue Book", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) return;

        StudentItem student = (StudentItem) students.getSelectedItem();
        BookItem book = (BookItem) books.getSelectedItem();
        if (student == null || book == null) return;

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Issue_Return (student_id,book_id,issue_date,status) " +
                            "VALUES (?,?,CURDATE(),'Issued')")) {
                ps.setInt(1, student.id);
                ps.setInt(2, book.id);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE Books SET quantity=quantity-1 WHERE book_id=? AND quantity>0")) {
                ps.setInt(1, book.id);
                if (ps.executeUpdate() == 0) {
                    con.rollback();
                    showError("This book is no longer available.");
                    return;
                }
            }

            con.commit();
            JOptionPane.showMessageDialog(this, "Book issued successfully!");
            showIssuedBooks();
        } catch (Exception e) {
            showError("Book could not be issued:\n" + e.getMessage());
        }
    }

    private void loadStudents(JComboBox<StudentItem> box) {
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT student_id,name FROM Students ORDER BY name")) {
            while (rs.next()) box.addItem(new StudentItem(rs.getInt(1), rs.getString(2)));
        } catch (Exception e) {
            showError("Unable to load students:\n" + e.getMessage());
        }
    }

    private void loadAvailableBooks(JComboBox<BookItem> box) {
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT book_id,title FROM Books WHERE quantity>0 ORDER BY title")) {
            while (rs.next()) box.addItem(new BookItem(rs.getInt(1), rs.getString(2)));
        } catch (Exception e) {
            showError("Unable to load books:\n" + e.getMessage());
        }
    }

    // ================= RETURN BOOK (DIALOG) =================
    // Clicking "Return Book" in sidebar opens this popup directly.
    // Shows a dropdown of currently issued books as "ID: <book_id> - <title>".
    // Selecting one and pressing OK marks it Returned and moves it into the
    // Returned Books list (status='Returned', return_date=today, quantity+1).
    private void returnBookDialog() {
        JComboBox<IssueItem> issues = new JComboBox<>();
        loadIssuedBooks(issues);

        if (issues.getItemCount() == 0) {
            showError("No issued books found to return.");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.add(new JLabel("Select Book to Return (ID - Title):"));
        panel.add(issues);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Return Book", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) return;

        IssueItem selected = (IssueItem) issues.getSelectedItem();
        if (selected == null) return;

        returnBook(selected.issueId);
    }

    private void loadIssuedBooks(JComboBox<IssueItem> box) {
        String sql = "SELECT ir.issue_id, b.book_id, b.title, s.name " +
                "FROM Issue_Return ir " +
                "JOIN Books b ON ir.book_id=b.book_id " +
                "JOIN Students s ON ir.student_id=s.student_id " +
                "WHERE ir.status='Issued' ORDER BY b.title";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                box.addItem(new IssueItem(
                        rs.getInt("issue_id"),
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("name")
                ));
            }
        } catch (Exception e) {
            showError("Unable to load issued books:\n" + e.getMessage());
        }
    }

    // ================= ISSUED / RETURNED =================
    private void showIssuedBooks() {
        showTransactions("Currently Issued Books", "Issued");
    }

    private void showReturnedBooks() {
        showTransactions("Returned Books", "Returned");
    }

    private void showTransactions(String heading, String status) {
        contentPanel.removeAll();
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(BG);

        JLabel title = new JLabel(heading);
        title.setFont(new Font("Arial", Font.BOLD, 29));
        title.setForeground(TEXT);
        title.setBorder(new EmptyBorder(25, 30, 20, 30));
        page.add(title, BorderLayout.NORTH);

        String[] columns = status.equals("Issued")
                ? new String[]{"Issue ID", "Student", "Book", "Issue Date", "Status"}
                : new String[]{"Issue ID", "Student", "Book", "Issue Date", "Return Date", "Status"};

        ArrayList<Object[]> rows = new ArrayList<>();
        String sql = "SELECT ir.issue_id,s.name,b.title,ir.issue_date,ir.return_date,ir.status " +
                "FROM Issue_Return ir JOIN Students s ON ir.student_id=s.student_id " +
                "JOIN Books b ON ir.book_id=b.book_id WHERE ir.status=? ORDER BY ir.issue_id DESC";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (status.equals("Issued")) {
                        rows.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getString(6)});
                    } else {
                        rows.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getDate(5), rs.getString(6)});
                    }
                }
            }
        } catch (Exception e) {
            showError("Unable to load records:\n" + e.getMessage());
        }

        JTable table = new JTable(rows.toArray(new Object[0][]), columns);
        styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 30, 15, 30));
        page.add(scroll, BorderLayout.CENTER);

        if (status.equals("Issued")) {
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottom.setBackground(BG);
            bottom.setBorder(new EmptyBorder(0, 30, 20, 30));

            JButton returnButton = new JButton("Return Selected Book");
            styleButton(returnButton, GREEN);
            returnButton.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row == -1) {
                    showError("Please select an issued book first.");
                    return;
                }
                int issueId = Integer.parseInt(table.getValueAt(row, 0).toString());
                returnBook(issueId);
            });
            bottom.add(returnButton);
            page.add(bottom, BorderLayout.SOUTH);
        }

        contentPanel.add(page);
        refresh();
    }

    private void returnBook(int issueId) {
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            int bookId;

            try (PreparedStatement find = con.prepareStatement(
                    "SELECT book_id FROM Issue_Return WHERE issue_id=? AND status='Issued'")) {
                find.setInt(1, issueId);
                try (ResultSet rs = find.executeQuery()) {
                    if (!rs.next()) {
                        showError("This issue record is already returned or does not exist.");
                        return;
                    }
                    bookId = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE Issue_Return SET return_date=CURDATE(),status='Returned' WHERE issue_id=?")) {
                ps.setInt(1, issueId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE Books SET quantity=quantity+1 WHERE book_id=?")) {
                ps.setInt(1, bookId);
                ps.executeUpdate();
            }

            con.commit();
            JOptionPane.showMessageDialog(this, "Book returned successfully!");
            showReturnedBooks();
        } catch (Exception e) {
            showError("Book could not be returned:\n" + e.getMessage());
        }
    }

    // ================= TABLE / BUTTON HELPERS =================
    // ================= TABLE / BUTTON HELPERS =================
    private void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setGridColor(new Color(225, 220, 235));
        table.setSelectionBackground(new Color(224, 215, 250));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);

        // ===== FIX: header ko custom renderer se draw karo =====
        // (Windows Look&Feel setBackground/setForeground ignore kar deta hai)
        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 42));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        t, value, false, false, row, column);
                label.setOpaque(true);
                label.setBackground(PRIMARY);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Arial", Font.BOLD, 14));
                label.setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(150, 125, 240)),
                        new EmptyBorder(0, 8, 0, 8)
                ));
                return label;
            }
        });

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 17, 10, 17));
    }

    private int getCount(String sql) {
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.out.println("Database: " + e.getMessage());
        }
        return 0;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Library Hub", JOptionPane.ERROR_MESSAGE);
    }

    private void refresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    static class StudentItem {
        int id;
        String name;
        StudentItem(int id, String name) { this.id = id; this.name = name; }
        public String toString() { return name; }
    }

    static class BookItem {
        int id;
        String title;
        BookItem(int id, String title) { this.id = id; this.title = title; }
        public String toString() { return title; }
    }

    static class IssueItem {
        int issueId;
        int bookId;
        String title;
        String studentName;
        IssueItem(int issueId, int bookId, String title, String studentName) {
            this.issueId = issueId;
            this.bookId = bookId;
            this.title = title;
            this.studentName = studentName;
        }
        public String toString() {
            return "ID: " + bookId + " - " + title + "  (" + studentName + ")";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }
            new Main().setVisible(true);
        });
    }
}