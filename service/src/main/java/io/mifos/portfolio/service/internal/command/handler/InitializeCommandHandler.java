/*
 * Copyright 2017 The Mifos Initiative.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mifos.portfolio.service.internal.command.handler;

import io.mifos.portfolio.api.v1.events.EventConstants;
import io.mifos.portfolio.service.ServiceConstants;
import io.mifos.portfolio.service.internal.command.InitializeServiceCommand;
import io.mifos.core.command.annotation.Aggregate;
import io.mifos.core.command.annotation.CommandHandler;
import io.mifos.core.command.annotation.EventEmitter;
import io.mifos.core.mariadb.domain.FlywayFactoryBean;
import io.mifos.portfolio.service.internal.util.RhythmAdapter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;

@SuppressWarnings({
    "unused"
})
@Aggregate
public class InitializeCommandHandler {

  private final Logger logger;
  private final DataSource dataSource;
  private final FlywayFactoryBean flywayFactoryBean;
  private final RhythmAdapter rhythmAdapter;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  public InitializeCommandHandler(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                  final DataSource dataSource,
                                  final FlywayFactoryBean flywayFactoryBean,
                                  final RhythmAdapter rhythmAdapter) {
    super();
    this.logger = logger;
    this.dataSource = dataSource;
    this.flywayFactoryBean = flywayFactoryBean;
    this.rhythmAdapter = rhythmAdapter;
  }

  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.INITIALIZE)
  public String initialize(final InitializeServiceCommand initializeServiceCommand) {
    this.logger.info("Start service migration.");
    this.flywayFactoryBean.create(this.dataSource).migrate();
    rhythmAdapter.request24Beats();
    return EventConstants.INITIALIZE;
  }
}
