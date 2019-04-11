package magicUWE.actions.context.sessionTransmissionCheck;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.DiagramSurfacePainter;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;

public class RuleViolationDiagramPainter extends DiagramSurfacePainter {

	@Override
	public void paint(Graphics g, DiagramPresentationElement diagram) {

		List<PresentationElement> symbols = diagram.getPresentationElements();
		for (PresentationElement pe : symbols) {

			RuleViolationTransmissionType violation =
				RuleViolationTransmissionType.getViolationForElement(
							Application.getInstance().getProject(), pe.getElement());
			if (violation != null) {
				g.setColor(Color.RED);

				Rectangle bounds = pe.getBounds();
				bounds.grow(4, 4);
				((Graphics2D) g).draw(bounds);
			}
		}
	}
}
