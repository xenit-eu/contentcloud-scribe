CREATE TABLE "evidence" (id UUID PRIMARY KEY);
ALTER TABLE "evidence" ADD COLUMN "name" text NULL;
CREATE UNIQUE INDEX CONCURRENTLY "evidence__name_idx" ON "evidence"("name");
ALTER TABLE "evidence" ADD COLUMN "description" text NULL;