package at.mana.idea.listener;

import at.mana.idea.component.plot.SingleSpectrumPlotModel;
import at.mana.idea.component.popup.SpectrumPopUpComponent;
import at.mana.idea.model.AnalysisModel;
import at.mana.idea.model.AnalysisModelComponent;
import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.service.AnalysisService;
import at.mana.idea.service.StorageService;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Andreas Schuler
 * @since 1.1
 */
public class EditorHoverListener
        implements EditorMouseListener, EditorMouseMotionListener {

    private final SpectrumPopUpComponent spectrumPopUpComponent = new SpectrumPopUpComponent();
    private final JComponent component = spectrumPopUpComponent.getComponent();
    private JBPopup popup;
    private final ComponentPopupBuilder builder = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(component, component);

    @Override
    public void mouseClicked(@NotNull EditorMouseEvent event) {
        Point point = new Point(event.getMouseEvent().getPoint());
        LogicalPosition pos = event.getEditor().xyToLogicalPosition(point);
        int offset = event.getEditor().logicalPositionToOffset(pos);
        Point offsetPoint = event.getEditor().offsetToXY(offset);
        if (offsetPoint.x > point.x) {
            offset -= 1;
        }
        AnalysisService service = AnalysisService.getInstance(Objects.requireNonNull(event.getEditor().getProject()));
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(event.getEditor().getProject());
        PsiFile file = documentManager.getPsiFile( event.getEditor().getDocument() );
        if(file instanceof PsiJavaFile) {
            AnalysisModel model = service.findDataFor( (PsiJavaFile) file );

            if(model == null ) return;

            Optional<Map.Entry<PsiMethod, AnalysisModelComponent>> method = model.getComponents().entrySet().stream()
                    .filter(m -> event.getEditor().getDocument().getLineNumber(m.getKey().getTextOffset() ) == pos.line ).findFirst();
            method.ifPresent(entry -> {
                if (isOverElement( event.getEditor().getDocument().getLineEndOffset( pos.line ) - event.getEditor().getDocument().getLineStartOffset(pos.line), pos )) {
                    if(this.popup != null) {
                        this.popup.dispose();
                        this.popup = null;
                    }
                    spectrumPopUpComponent.updateModel( 0, new SingleSpectrumPlotModel( new double[  ]{ entry.getValue().getPowerCoefficient() } ));
                    spectrumPopUpComponent.updateModel( 1, new SingleSpectrumPlotModel( new double[  ]{ entry.getValue().getFrequencyCoefficient() } ));
                    spectrumPopUpComponent.updateModel( 2, new SingleSpectrumPlotModel( new double[  ]{ entry.getValue().getDurationCoefficient() } ));
                    this.popup = this.builder.createPopup();
                    Point location = event.getMouseEvent().getLocationOnScreen();
                    location.y = location.y + event.getEditor().getLineHeight();
                    this.popup.show(RelativePoint.fromScreen(location));
                    // TODO: make position of popup relative to current line + 1
                }
                else {
                    service.clearSelectedMethod();
                }
                event.getEditor().getContentComponent().repaint();
            } );
            if(method.isEmpty()) {
                service.clearSelectedMethod();
                event.getEditor().getContentComponent().setCursor( Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR) );
                event.getEditor().getContentComponent().repaint();
                if(popup != null)
                    popup.dispose();

            }
        }
    }

    private boolean isOverElement( int offset, LogicalPosition position ) {
        // TODO: specify correct offset
        return position.column >= offset + 5 && position.column <= offset + 30;
    }

    @Override
    public void mouseMoved(@NotNull EditorMouseEvent event) {
        Point point = new Point(event.getMouseEvent().getPoint());
        LogicalPosition pos = event.getEditor().xyToLogicalPosition(point);
        int offset = event.getEditor().logicalPositionToOffset(pos);
        Point offsetPoint = event.getEditor().offsetToXY(offset);
        if (offsetPoint.x > point.x) {
            offset -= 1;
        }
        AnalysisService service = AnalysisService.getInstance(Objects.requireNonNull(event.getEditor().getProject()));
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(event.getEditor().getProject());
        PsiFile file = documentManager.getPsiFile( event.getEditor().getDocument() );
        if(file instanceof PsiJavaFile) {
            AnalysisModel model = service.findDataFor( (PsiJavaFile) file );

            if(model == null ) return;

            Optional<PsiMethod> method = model.getComponents().keySet().stream()
                    .filter(m -> event.getEditor().getDocument().getLineNumber(m.getTextOffset() ) == pos.line ).findFirst();
            method.ifPresent(psiMethod -> {
                if (isOverElement( event.getEditor().getDocument().getLineEndOffset( pos.line ) - event.getEditor().getDocument().getLineStartOffset(pos.line), pos )) {
                    service.setSelectedMethod(psiMethod);
                    event.getEditor().getContentComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                else {
                    service.clearSelectedMethod();
                }
                event.getEditor().getContentComponent().repaint();
            } );
            if(method.isEmpty()) {
                service.clearSelectedMethod();
                event.getEditor().getContentComponent().setCursor( Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR) );
                event.getEditor().getContentComponent().repaint();
                if(this.popup != null) {
                    this.popup.dispose();
                    this.popup = null;
                }
            }
        }
    }
}
