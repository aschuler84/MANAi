package at.mana.idea.service.impl;

import at.mana.ManaConfiguration;
import at.mana.idea.service.VisualisationService;
import com.intellij.util.pico.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

public class VisualizationServiceImpl implements VisualisationService {

    private final MutablePicoContainer container = new DefaultPicoContainer();

    private ApplicationContext context;

    public VisualizationServiceImpl() {

    }


    @Override
    public <T> T getBean( Class<T> clazz ) {
        if( this.context == null ) {
            this.context = SpringApplication.run( ManaConfiguration.class );
        }
        return context.getBean( clazz );
    }



}
