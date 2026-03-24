-- Document Management Service – Database Schema

-- La tabla de documentos con un Array nativo de "tags" text[]
CREATE TABLE IF NOT EXISTS documents (
    id UUID PRIMARY KEY,
    upload_user VARCHAR(100) NOT NULL,
    document_name VARCHAR(255) NOT NULL,
    minio_path TEXT NOT NULL,
    tags TEXT[],
    file_size BIGINT,
    file_type VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

-- Índices eficientes para las búsquedas (B-TREE para user/names y GIN para text[])
CREATE INDEX IF NOT EXISTS idx_documents_user ON documents(upload_user);
CREATE INDEX IF NOT EXISTS idx_documents_name ON documents(document_name);
CREATE INDEX IF NOT EXISTS idx_documents_tags ON documents USING GIN(tags);
CREATE INDEX IF NOT EXISTS idx_documents_created ON documents(created_at DESC);
