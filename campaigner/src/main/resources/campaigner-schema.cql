CREATE KEYSPACE gridbandit WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
USE gridbandit;

  CREATE TABLE campaigns (
    name text,
    active_template_ids set<text>,
    template_id_to_weight map<text, double>,
    scoring_formula text,
    last_potentially_score_altering_change_mse bigint,
    template_weights_include_changes_up_to_mse bigint,
    template_weights_update_heartbeat_mse bigint,
    PRIMARY KEY (name)
  );

  CREATE TABLE messages (
    message_id text,
    campaign_name text,
    template_id text,
    event_timestamp_mse_colon_metric_to_value map<text, double>,
    PRIMARY KEY ((campaign_name, template_id), message_id)
  );