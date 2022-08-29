package dev.buijs.klutter.jetbrains.shared

import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JPanel

object KlutterBanner : JPanel() {

    override fun getPreferredSize(): Dimension =
        Dimension(500, 150)

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        KlutterIcons.banner.paintIcon(this, g, 0, 25)
    }
}