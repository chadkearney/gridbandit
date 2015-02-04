package it.gridband.campaigner.select;

import com.google.common.base.Optional;
import it.gridband.campaigner.model.Campaign;

public interface ActiveTemplateIdSelector {
	Optional<String> select(Campaign campaign);
}
