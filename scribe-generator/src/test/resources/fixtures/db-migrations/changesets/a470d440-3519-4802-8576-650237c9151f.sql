CREATE TABLE "case" (id UUID PRIMARY KEY);
ALTER TABLE "case" ADD COLUMN "name" text NOT NULL;
CREATE UNIQUE INDEX CONCURRENTLY "case__name_idx" ON "case"("name");
ALTER TABLE "case" ADD COLUMN "description" text NULL;
CREATE INDEX CONCURRENTLY "case__description_idx" ON "case"("description");
CREATE TABLE "person" (id UUID PRIMARY KEY);
ALTER TABLE "person" ADD COLUMN "name" text NULL;
ALTER TABLE "person" ADD COLUMN "notes" text NULL;