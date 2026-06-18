CREATE TABLE storages (
    id SERIAL PRIMARY KEY,
    storage_type VARCHAR NOT NULL UNIQUE,
    bucket VARCHAR NOT NULL,
    path VARCHAR NOT NULL,
    CONSTRAINT uq_storages_bucket_path UNIQUE (bucket, path)
);