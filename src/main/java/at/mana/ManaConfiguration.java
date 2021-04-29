package at.mana;


import at.mana.template.TemplateConfiguration;
import at.mana.visualisation.VisualisationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({VisualisationConfiguration.class, TemplateConfiguration.class})
public class ManaConfiguration {



}
